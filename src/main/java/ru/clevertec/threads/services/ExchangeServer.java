package ru.clevertec.threads.services;

import ru.clevertec.threads.models.Request;
import ru.clevertec.threads.models.Response;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Class for accepting requests and returning new values depending on the exchange rate
 */
public class ExchangeServer {
    private static final int MAX_REQUESTS_IN_WORK = 10;
    private static final int DELAY = 2000;

    private final Random random;
    private final Semaphore semaphore;
    private final int exchangeRate;

    public ExchangeServer(int exchangeRate) {
        this.exchangeRate = exchangeRate;
        this.random = new Random();
        this.semaphore = new Semaphore(MAX_REQUESTS_IN_WORK);
    }

    /**
     * Exchange request value depending on the exchange rate and return new value
     *
     * @param request contain value for calculating
     * @return response with exchanged value from request
     * @throws InterruptedException Thrown when a thread is interrupted
     */
    public Response exchangeMoney(Request request) throws InterruptedException {
        semaphore.acquire();
        delay();
        semaphore.release();
        return new Response(exchangeRate * request.value());
    }

    /**
     * Set a delay for current thread
     *
     * @throws InterruptedException Thrown when a thread is interrupted
     */
    private void delay() throws InterruptedException {
        int timeToSleep = random.nextInt(DELAY);
        TimeUnit.MILLISECONDS.sleep(timeToSleep);
    }
}
