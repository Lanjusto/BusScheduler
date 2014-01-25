package ru.lanjusto.busscheduler.server.api;

import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.RouteStop;

import java.util.List;

/**
 * Created by estroykov on 18.01.14.
 */
@ImplementedBy(DataProvider.class)
public interface IDataProvider {
    @NotNull
    List<Route> getRoutes();

    @NotNull
    List<RouteStop> getRouteStops(long routeId);
}
