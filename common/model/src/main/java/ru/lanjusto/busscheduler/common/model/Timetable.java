package ru.lanjusto.busscheduler.common.model;

import java.util.List;

/**
 * Расписание
 */
public class Timetable {
    private final List<Time> timeList;

    public Timetable(List<Time> timeList) {
        this.timeList = timeList;
    }

    public List<Time> getTimeList() {
        return timeList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Timetable: ");
        sb.append("\n");
        for (Time time : getTimeList()) {
            sb.append("   ");
            sb.append(time.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}

