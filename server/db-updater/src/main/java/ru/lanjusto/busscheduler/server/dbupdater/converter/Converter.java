package ru.lanjusto.busscheduler.server.dbupdater.converter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by estroykov on 03.12.13.
 */
public class Converter {
    public static int toInteger(@NotNull String s) {
        return Integer.valueOf(s.trim());
    }
}