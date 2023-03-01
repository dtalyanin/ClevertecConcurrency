package ru.clevertec.threads.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.threads.models.Request;
import ru.clevertec.threads.models.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeServerTest {
    private ExchangeServer server;

    @BeforeEach
    void setUp() {
        server = new ExchangeServer(10);
    }

    @Test
    void checkExchangeMoneyShouldReturnExchangedMoney() throws InterruptedException {
        Request request = new Request(10);

        int expectedMoney = 100;
        int actualMoney = server.exchangeMoney(request).exchangedMoney();

        assertEquals(actualMoney, expectedMoney);
    }

    @Test
    void checkExchangeMoneyShouldReturnAllResponsesFromThreads() throws InterruptedException, ExecutionException {
        List<Request> requests = IntStream.range(0, 40).mapToObj(Request::new).toList();
        List<Callable<Response>> callables = requests.stream()
                .map(request -> (Callable<Response>) () -> server.exchangeMoney(request))
                .toList();

        ExecutorService service = Executors.newFixedThreadPool(20);
        List<Future<Response>> futures = service.invokeAll(callables);
        List<Response> actual = new ArrayList<>();
        for (Future<Response> future : futures) {
            actual.add(future.get());
        }
        service.shutdown();

        int expectedSize = requests.size();
        int actualSize = actual.size();

        assertEquals(expectedSize, actualSize);
    }
}