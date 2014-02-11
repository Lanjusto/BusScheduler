package ru.lanjusto.busscheduler.server.api.restlets;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.server.api.IDataProvider;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailable;

public class RouteRestlet extends AbstractRestlet {
    public RouteRestlet(@NotNull IDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    protected Object handle(Long routeId) throws NoTimetableAvailable {
        return dataProvider.getRouteStops(routeId);
    }
}
