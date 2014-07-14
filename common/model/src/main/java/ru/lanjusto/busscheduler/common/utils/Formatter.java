package ru.lanjusto.busscheduler.common.utils;

public final class Formatter {
    private Formatter() {

    }

    public static String getHours(int n) {
        return getForm(n, "час", "часа", "часов");
    }

    public static String getMinutes(int n) {
        return getForm(n, "минуту", "минуты", "минут");
    }

    public static String getForm(int n, String f1, String f2, String f5) {
        final int a = Math.abs(n) % 100;
        final int b = Math.abs(n) % 10;

        if (b == 1 && a != 11) {
            return n + " " + f1;
        } else if (b >= 2 && b <= 4 && a != 12 && a != 13 && a != 14) {
            return n + " " + f2;
        } else {
            return n + " " + f5;
        }
    }
}
