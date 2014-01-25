package ru.lanjusto.busscheduler.common.model;

import com.google.common.base.Strings;
import ru.lanjusto.busscheduler.common.utils.Assert;

/**
 * Время
 */
public class Time {
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

    @Override
    public String toString() {
        return Strings.padStart(String.valueOf(getHours()), 2, '0') + ":" +
               Strings.padStart(String.valueOf(getMinutes()), 2, '0');
    }
}