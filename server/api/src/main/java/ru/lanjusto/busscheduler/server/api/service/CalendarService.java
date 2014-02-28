package ru.lanjusto.busscheduler.server.api.service;

import ru.lanjusto.busscheduler.server.api.timetable.Day;

import java.util.Calendar;

public class CalendarService {
    public static Day getDay() {
        final Calendar calendar = Calendar.getInstance();

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
                return Day.WORKDAY;
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
                return Day.WEEKEND;
            default:
                throw new IllegalArgumentException();
        }
    }
}
