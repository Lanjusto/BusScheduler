package ru.lanjusto.busscheduler.common.utils;

/**
 * Невыполнение условия в Assert.
 */
public class AssertException extends RuntimeException {
    public AssertException() {
    }

    public AssertException(String message) {
        super(message);
    }
}
