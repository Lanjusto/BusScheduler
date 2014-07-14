package ru.lanjusto.busscheduler.common.model;

import com.google.common.base.Strings;
import ru.lanjusto.busscheduler.common.utils.Assert;
import ru.lanjusto.busscheduler.common.utils.Formatter;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Время
 */
public class Time {
    private final static int HOURS_IN_DAY = 24;
    private final static int MINUTES_IN_HOUR = 60;

    private final int hours;
    private final int minutes;

    public static Time fromString(String s) {
        final int h = Integer.valueOf(s.substring(0, 2));
        final int m = Integer.valueOf(s.substring(3, 5));
        return new Time(h, m);
    }

    public Time(int hours, int minutes) {
        Assert.isTrue(hours >= 0 && hours <= 23);
        Assert.isTrue(minutes >= 0 && minutes <= 59);

        this.hours = hours;
        this.minutes = minutes;
    }

    public static Time now() {
        //TODO поставить часовой пояс в зависимость от настроенного города
        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
        return new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public Time add(Time t) {
        int h = getHours() + t.getHours();
        int m = getMinutes() + t.getMinutes();

        h = (h + m / MINUTES_IN_HOUR) % HOURS_IN_DAY;
        m = m % MINUTES_IN_HOUR;

        return new Time(h, m);
    }

    public Time substract(Time t) {
        int h = (getHours() - t.getHours()) % HOURS_IN_DAY;
        int m = (getMinutes() - t.getMinutes());

        if (m < 0) {
            m += MINUTES_IN_HOUR;
            h--;
        }
        if (h < 0) {
            h += HOURS_IN_DAY;
        }

        h = h + m / MINUTES_IN_HOUR;
        m = m % MINUTES_IN_HOUR;

        return new Time(h, m);
    }

    public static int toMinutes(Time time) {
        return time.getHours() * MINUTES_IN_HOUR + time.getMinutes();
    }

    public boolean isZero() {
        return getHours() == 0 && getMinutes() == 0;
    }

    public static Time inverse(Time t) {
        return Time.fromString("00:00").substract(t);
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

    public String toHumanString() {
        if (getHours() == 0) {
            return Formatter.getMinutes(getMinutes());
        } else if (getMinutes() == 0) {
            return Formatter.getHours(getHours());
        } else {
            return Formatter.getHours(getHours()) + " " + Formatter.getMinutes(getMinutes());
        }
    }
}