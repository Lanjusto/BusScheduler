package ru.lanjusto.busscheduler.server.api.service;


import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.Time;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.common.utils.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimetableService {
    public static Timetable interpolate(@NotNull Timetable timetable1, @NotNull Timetable timetable2, double f) {
        final List<Time> times1 = timetable1.getTimeList();
        final List<Time> times2 = timetable2.getTimeList();

        Assert.equals(times1.size(), times2.size());

        final List<Time> times = new ArrayList<Time>();

        for (int i = 0; i < times1.size(); i++) {
            final Time t1 = times1.get(i);
            final Time t2 = times2.get(i);

            final Time t = interpolate(t1, t2, f);
            times.add(t);
        }
        return new Timetable(times);
    }

    static Time interpolate(@NotNull Time t1, @NotNull Time t2, double f) {
        final double q1 = 1 / (f + 1);
        final double q2 = f / (f + 1);

        int average = (int) ((q1 * Time.toMinutes(t1) + q2 * Time.toMinutes(t2)));
        return Time.fromMinutes(average);
    }

    public static Timetable sortTimes(@NotNull Timetable timetable) {
        return sortTimes(timetable, Time.fromString("00:00"));
    }

    public static Timetable sortTimes(@NotNull Timetable timetable, @NotNull final Time startTime) {
        final List<Time> times = new ArrayList<Time>(timetable.getTimeList());
        Collections.sort(times, new Comparator<Time>() {
            @Override
            public int compare(Time o1, Time o2) {
                int result = o1.getHours() - o2.getHours();
                if (result != 0) {
                    return result;
                }

                return o1.getMinutes() - o2.getMinutes();
            }
        });

        return new Timetable(times);
    }
}
