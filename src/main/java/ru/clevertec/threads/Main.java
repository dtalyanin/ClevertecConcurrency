package ru.clevertec.threads;

import ru.clevertec.threads.models.Request;
import ru.clevertec.threads.services.Client;
import ru.clevertec.threads.services.ExchangeServer;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExchangeServer exchangeServer = new ExchangeServer(100);
        Client client = new Client(exchangeServer);
        List<Request> requests = IntStream.range(0, 20).mapToObj(Request::new).toList();
        client.sendRequests(requests);
        client.getResponses().forEach(System.out::println);
    }
}