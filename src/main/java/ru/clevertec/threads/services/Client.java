package ru.clevertec.threads.services;

import ru.clevertec.threads.exceptions.ThreadException;
import ru.clevertec.threads.models.Request;
import ru.clevertec.threads.models.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Client class for sending requests to server
 */
public class Client {
    private static final int MAX_THREADS_TO_SEND = 20;

    private final ExchangeServer server;
    private final List<Response> responses;
    private final Lock lock;

    public Client(ExchangeServer server) {
        this.server = server;
        this.responses = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    /**
     * Send list of request to server in different threads and add all responses to other list
     *
     * @param requests list of request, that should be sent
     */
    public void sendRequests(List<Request> requests) {
        int numberOfThreads = Math.min(requests.size(), MAX_THREADS_TO_SEND);
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<Response>> callables = requests.stream()
                .map(request -> (Callable<Response>) () -> server.exchangeMoney(request))
                .toList();
        lock.lock();
        try {
            List<Future<Response>> futures = service.invokeAll(callables);
            for (Future<Response> future : futures) {
                responses.add(future.get());
            }
            service.shutdown();
        } catch (ExecutionException | InterruptedException e) {
            throw new ThreadException("Exception in threads: " + e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get all responses that were received from previously sent requests to the server
     *
     * @return list of responses from the server with exchanged values
     */
    public List<Response> getResponses() {
        lock.lock();
        try {
            List<Response> responsesToSend = new ArrayList<>(responses);
            responses.clear();
            return responsesToSend;
        } finally {
            lock.unlock();
        }
    }
}
