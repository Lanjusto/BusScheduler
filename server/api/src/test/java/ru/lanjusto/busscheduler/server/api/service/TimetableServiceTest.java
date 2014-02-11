package ru.lanjusto.busscheduler.server.api.service;


import org.junit.Test;
import ru.lanjusto.busscheduler.common.model.Time;

import static org.junit.Assert.assertEquals;

public class TimetableServiceTest {
    @Test
    public void simpleTest() {
        test("00:00", "01:00", 1, "00:30");
        test("14:15", "15:15", 1, "14:45");
        test("14:15", "15:15", 3, "15:00");
    }

    private void test(String t1, String t2, double d, String t) {
        assertEquals(stringToTime(t), TimetableService.interpolate(stringToTime(t1), stringToTime(t2), d));
    }

    private Time stringToTime(String s) {
        final int h = Integer.valueOf(s.substring(0, 2));
        final int m = Integer.valueOf(s.substring(3, 5));
        return new Time(h, m);
    }
}
