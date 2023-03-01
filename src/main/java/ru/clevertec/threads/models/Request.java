package ru.clevertec.threads.models;

/**
 * Class, that contain integer value for sending to the server
 * @param value will be sent to the server
 */
public record Request(int value) {
}