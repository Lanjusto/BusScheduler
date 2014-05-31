package ru.lanjusto.busscheduler.server.api.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.lanjusto.busscheduler.common.model.RouteStop;

import java.util.List;

public class RouteService {
    @Nullable
    public RouteStop getPrevious(@NotNull RouteStop routeStop) {
        final List<RouteStop> routeStopList = routeStop.getRoute().getRouteStops(routeStop.getDirection());
        final int currentIndex = routeStopList.indexOf(routeStop);

        if (currentIndex == 0) {
            //todo проверить круговые маршруты
            return null;
        } else {
            return routeStopList.get(currentIndex - 1);
        }
    }

    @Nullable
    public RouteStop getNext(@NotNull RouteStop routeStop) {
        //TODO двусвязный список?!
        final List<RouteStop> routeStopList = routeStop.getRoute().getRouteStops(routeStop.getDirection());
        final int currentIndex = routeStopList.indexOf(routeStop);

        if (currentIndex == routeStopList.size() - 1) {
            return null;
        } else {
            return routeStopList.get(currentIndex + 1);
        }
    }
}
