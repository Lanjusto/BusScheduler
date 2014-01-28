package ru.lanjusto.busscheduler.client;


public class DataIsNotAvailableException extends Exception {
    public DataIsNotAvailableException(Throwable cause) {
        super("Data is not available", cause);
    }
}
