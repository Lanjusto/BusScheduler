package ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.ru;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.server.api.timetable.ITimetableGetter;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailable;

public class MgtRuTimetableGetter implements ITimetableGetter {
    @NotNull
    @Override
    public Timetable get(@NotNull RouteStop routeStop) throws NoTimetableAvailable {
        throw new NoTimetableAvailable();
    }
}
