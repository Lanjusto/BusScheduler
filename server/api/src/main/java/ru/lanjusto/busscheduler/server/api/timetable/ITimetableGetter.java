package ru.lanjusto.busscheduler.server.api.timetable;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.Timetable;

/**
 * Достаём расписания
 */
public interface ITimetableGetter {
    @NotNull
    Timetable get(@NotNull RouteStop routeStop) throws NoTimetableAvailable;
}
