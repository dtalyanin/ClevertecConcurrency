package ru.clevertec.threads.exceptions;

/**
 * Wrapper exception for exceptions from thread (Execution exception and Interrupted exception)
 */
public class ThreadException extends RuntimeException {
    public ThreadException(String message, Throwable cause) {
        super(message, cause);
    }
}
