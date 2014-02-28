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
    public void addTimeTest() {
        assertEquals(Time.fromString("15:17"), Time.fromString("14:42").add(Time.fromString("00:35")));
        assertEquals(Time.fromString("17:17"), Time.fromString("14:42").add(Time.fromString("02:35")));
        assertEquals(Time.fromString("18:58"), Time.fromString("05:59").add(Time.fromString("12:59")));
        assertEquals(Time.fromString("01:33"), Time.fromString("22:59").add(Time.fromString("02:34")));
        assertEquals(Time.fromString("00:00"), Time.fromString("00:01").add(Time.fromString("23:59")));
        assertEquals(Time.fromString("10:17"), Time.fromString("00:00").add(Time.fromString("10:17")));
        assertEquals(Time.fromString("10:17"), Time.fromString("10:17").add(Time.fromString("00:00")));
        assertEquals(Time.fromString("23:13"), Time.fromString("23:58").add(Time.fromString("23:15")));
    }

    @Test
    public void substractTimeTest() {
        assertEquals(Time.fromString("14:42"), Time.fromString("15:17").substract(Time.fromString("00:35")));
        assertEquals(Time.fromString("14:42"), Time.fromString("17:17").substract(Time.fromString("02:35")));
        assertEquals(Time.fromString("05:59"), Time.fromString("18:58").substract(Time.fromString("12:59")));
        assertEquals(Time.fromString("22:59"), Time.fromString("01:33").substract(Time.fromString("02:34")));
        assertEquals(Time.fromString("00:01"), Time.fromString("00:00").substract(Time.fromString("23:59")));
        assertEquals(Time.fromString("00:00"), Time.fromString("10:17").substract(Time.fromString("10:17")));
        assertEquals(Time.fromString("10:17"), Time.fromString("10:17").substract(Time.fromString("00:00")));
        assertEquals(Time.fromString("23:58"), Time.fromString("23:13").substract(Time.fromString("23:15")));
    }

    @Test
    public void sortTest1() {
        final List<Time> times = new ArrayList<Time>();
        times.add(Time.fromString("23:19"));
        times.add(Time.fromString("14:15"));
        times.add(Time.fromString("19:47"));
        times.add(Time.fromString("00:00"));
        times.add(Time.fromString("23:59"));
        times.add(Time.fromString("04:58"));
        times.add(Time.fromString("07:00"));

        final Timetable timetable = new Timetable(times);
        final Timetable sortedTimetable = TimetableService.sortTimes(timetable);

        int i = -1;
        assertEquals("00:00", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("04:58", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("07:00", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("14:15", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("19:47", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("23:19", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("23:59", sortedTimetable.getTimeList().get(++i).toString());
    }

    @Test
    public void sortTest2() {
        final List<Time> times = new ArrayList<Time>();
        times.add(Time.fromString("23:19"));
        times.add(Time.fromString("14:15"));
        times.add(Time.fromString("19:47"));
        times.add(Time.fromString("00:00"));
        times.add(Time.fromString("23:59"));
        times.add(Time.fromString("23:15"));
        times.add(Time.fromString("04:58"));
        times.add(Time.fromString("23:13"));
        times.add(Time.fromString("07:00"));

        final Timetable timetable = new Timetable(times);
        final Timetable sortedTimetable = TimetableService.sortTimes(timetable, Time.fromString("23:15"));

        int i = -1;
        assertEquals("23:15", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("23:19", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("23:59", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("00:00", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("04:58", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("07:00", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("14:15", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("19:47", sortedTimetable.getTimeList().get(++i).toString());
        assertEquals("23:13", sortedTimetable.getTimeList().get(++i).toString());
    }
}
