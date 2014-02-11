package ru.lanjusto.busscheduler.server.api.timetable;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.org.MgtOrgTimetableGetter;
import ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.ru.MgtRuTimetableGetter;

public class GeneralTimetableGetter implements ITimetableGetter {
    private final ITimetableGetter orgGetter;
    private final ITimetableGetter ruGetter;

    public GeneralTimetableGetter() {
        this.orgGetter = new MgtOrgTimetableGetter();
        this.ruGetter = new MgtRuTimetableGetter();
    }

    @NotNull
    @Override
    public Timetable get(@NotNull RouteStop routeStop) throws NoTimetableAvailable {
        try {
            return orgGetter.get(routeStop);
        } catch (NoTimetableAvailable e) {
            return ruGetter.get(routeStop);
        }
    }
}
