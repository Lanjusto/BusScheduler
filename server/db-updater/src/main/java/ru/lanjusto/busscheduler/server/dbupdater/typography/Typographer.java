package ru.lanjusto.busscheduler.server.dbupdater.typography;

import org.jetbrains.annotations.NotNull;

public class Typographer {
    private Typographer () {

    }

    @NotNull
    public static String process (@NotNull String text) {
        return text
                .replaceAll(" - ", " — ")
                .replaceAll("^\"", "«")
                .replaceAll("\"$", "»")
                .replaceAll(" \"", " «")
                .replaceAll("\" ", "» ");
    }
}
