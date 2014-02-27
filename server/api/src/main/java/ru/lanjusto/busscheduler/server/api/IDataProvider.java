package ru.lanjusto.busscheduler.server.api;

import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailableException;

import java.util.List;

@ImplementedBy(DataProvider.class)
public interface IDataProvider {
    @NotNull
    List<Route> getRoutes();

    @NotNull
    List<RouteStop> getRouteStops(long routeId);

    @NotNull
    Timetable getTimeTable(long routeId) throws NoTimetableAvailableException;
}
