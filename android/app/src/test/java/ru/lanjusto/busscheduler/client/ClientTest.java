package ru.lanjusto.busscheduler.client;


import org.junit.Test;
import ru.lanjusto.busscheduler.common.model.RouteStop;

import java.util.List;

public class ClientTest {
    @Test
    public void getRouteStopsTest() throws DataIsNotAvailableException {
        final List<RouteStop> routeStopList = new Client().getRouteStops(732L);
        for (RouteStop routeStop : routeStopList) {
            System.out.println(routeStop);
        }
    }
}
