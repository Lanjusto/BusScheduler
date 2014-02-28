package ru.lanjusto.busscheduler.server.api.service;


import org.junit.Test;
import ru.lanjusto.busscheduler.common.model.Time;
import ru.lanjusto.busscheduler.common.model.Timetable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TimetableServiceTest {
    @Test
    public void simpleTest() {
        test("00:00", "01:00", 1, "00:30");
        test("14:15", "15:15", 1, "14:45");
        test("14:15", "15:15", 3, "15:00");
    }

    private void test(String t1, String t2, double d, String t) {
        assertEquals(Time.fromString(t), TimetableService.interpolate(Time.fromString(t1), Time.fromString(t2), d));
    }

    @Test
    public void sortTest() {
        final List<Time> times = new ArrayList<Time>();
        times.add(Time.fromString("23:19"));
        times.add(Time.fromString("14:15"));
        times.add(Time.fromString("19:47"));
        times.add(Time.fromString("00:00"));
        times.add(Time.fromString("23:59"));
        times.add(Time.fromString("04:58"));
        times.add(Time.fromString("07:00"));


        final Timetable timetable = new Timetable(times);
        final Timetable sortedTimetable = TimetableService.sortTimes(timetable, Time.fromString("23:30"));
        System.out.println(sortedTimetable);
    }
}
