package ru.lanjusto.busscheduler.common.model;

import com.google.common.base.Strings;
import ru.lanjusto.busscheduler.common.utils.Assert;

/**
 * Время
 */
public class Time {
    private final static int MINUTES_IN_HOUR = 60;

    private final int hours;
    private final int minutes;

    public Time(int hours, int minutes) {
        Assert.isTrue(hours >= 0 && hours <= 23);
        Assert.isTrue(minutes >= 0 && minutes <= 59);

        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public static int toMinutes(Time time) {
        return time.getHours() * MINUTES_IN_HOUR + time.getMinutes();
    }

    public static Time fromMinutes(int minutes) {
        final int h = minutes / MINUTES_IN_HOUR;
        final int m = minutes - h * MINUTES_IN_HOUR;
        return new Time(h, m);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Time)) {
            return false;
        }

        Time time = (Time) o;

        if (hours != time.hours) {
            return false;
        }
        if (minutes != time.minutes) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = hours;
        result = 31 * result + minutes;
        return result;
    }

    @Override
    public String toString() {
        return Strings.padStart(String.valueOf(getHours()), 2, '0') + ":" +
               Strings.padStart(String.valueOf(getMinutes()), 2, '0');
    }
}