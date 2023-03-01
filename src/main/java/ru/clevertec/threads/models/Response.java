package ru.clevertec.threads.models;

/**
 * Class, that contain returned integer exchanged value from the server
 * @param exchangedValue returned exchanged value from the server
 */
public record Response(int exchangedValue) {
}