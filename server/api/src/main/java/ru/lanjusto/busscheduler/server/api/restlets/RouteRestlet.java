package ru.lanjusto.busscheduler.server.api.restlets;

import org.jetbrains.annotations.NotNull;
import org.restlet.Request;
import org.restlet.Response;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.server.api.IDataProvider;

import java.util.List;

public class RouteRestlet extends AbstractRestlet {
    public RouteRestlet(@NotNull IDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    public void handle(Request request, Response response) {
        final long routeId = getRouteId(request);
        final List<RouteStop> routeStops = dataProvider.getRouteStops(routeId);

        packToXml(response, routeStops);
    }
}
