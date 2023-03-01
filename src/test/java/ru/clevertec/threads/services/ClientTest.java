package ru.clevertec.threads.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.threads.models.Request;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientTest {
    @Spy
    private ExchangeServer server = new ExchangeServer(10);
    @InjectMocks
    private Client client;

    @Test
    void checkGetResponsesShouldReturnEmptyListIfNoRequests() throws InterruptedException {
        int expectedSize = 0;
        int actualSize = client.getResponses().size();

        verify(server, never()).exchangeMoney(any(Request.class));
        assertEquals(expectedSize, actualSize);
    }

    @Test
    void checkGetResponsesShouldReturn10ResponsesFromOneThread() throws InterruptedException {
        List<Request> requests = getRequestsInRange(10);
        client.sendRequests(requests);
        int expectedSize = 10;
        int actualSize = client.getResponses().size();

        verify(server, times(10)).exchangeMoney(any(Request.class));
        assertEquals(expectedSize, actualSize);
    }

    @Test
    void checkGetResponsesShouldReturn20ResponsesFrom2Threads() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            client.sendRequests(getRequestsInRange(10));
            countDownLatch.countDown();
        }).start();
        new Thread(() -> {
            client.sendRequests(getRequestsInRange(10));
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();

        int expectedSize = 20;
        int actualSize = client.getResponses().size();

        verify(server, times(20)).exchangeMoney(any(Request.class));
        assertEquals(expectedSize, actualSize);
    }

    @Test
    void checkGetResponsesShouldReturn5ResponsesOneThreadAlreadyGetResponses() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            client.sendRequests(getRequestsInRange(10));
            countDownLatch.countDown();
        }).start();
        new Thread(() -> {
            sleepThread(200);
            client.getResponses();
            client.sendRequests(getRequestsInRange(5));
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();

        int expectedSize = 5;
        int actualSize = client.getResponses().size();

        verify(server, times(15)).exchangeMoney(any(Request.class));
        assertEquals(expectedSize, actualSize);
    }

    private List<Request> getRequestsInRange(int range) {
        return IntStream.range(0, range).mapToObj(Request::new).toList();
    }

    private void sleepThread(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.err.println("Interrupted exception in " + Thread.currentThread().getName());
        }
    }
}