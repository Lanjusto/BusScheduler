package ru.lanjusto.busscheduler.server.dbupdater.converter;

import org.jetbrains.annotations.NotNull;

public class Converter {
    public static int toInteger(@NotNull String s) {
        return Integer.valueOf(s.trim());
    }
}