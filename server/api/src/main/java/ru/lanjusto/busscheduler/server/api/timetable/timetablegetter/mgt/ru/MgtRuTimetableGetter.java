package ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.ru;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.server.api.timetable.Day;
import ru.lanjusto.busscheduler.server.api.timetable.ITimetableGetter;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailableException;

public class MgtRuTimetableGetter implements ITimetableGetter {
    @NotNull
    @Override
    public Timetable get(@NotNull RouteStop routeStop, @NotNull Day day) throws NoTimetableAvailableException {
        throw new NoTimetableAvailableException();
    }
}
