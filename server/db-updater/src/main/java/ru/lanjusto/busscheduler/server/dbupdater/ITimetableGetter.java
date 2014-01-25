package ru.lanjusto.busscheduler.server.dbupdater;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.Timetable;

/**
 * Достаём расписания
 */
public interface ITimetableGetter {
    @NotNull
    Timetable get(@NotNull Route route);
}
