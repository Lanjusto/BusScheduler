package ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.ru;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.server.api.timetable.ITimetableGetter;

public class MgtRuTimetableGetter implements ITimetableGetter {
    @NotNull
    @Override
    public Timetable get(@NotNull Route route) {
        throw new UnsupportedOperationException();
    }
}
