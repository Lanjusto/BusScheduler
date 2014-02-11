package ru.lanjusto.busscheduler.server.api.restlets;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.server.api.IDataProvider;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailable;

public class RoutesListRestlet extends AbstractRestlet {
    public RoutesListRestlet(@NotNull IDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    protected Object handle(Long routeId) throws NoTimetableAvailable {
        return dataProvider.getRoutes();
    }
}
