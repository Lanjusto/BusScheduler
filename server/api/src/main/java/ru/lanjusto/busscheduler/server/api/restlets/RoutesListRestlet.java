package ru.lanjusto.busscheduler.server.api.restlets;

import org.jetbrains.annotations.NotNull;
import org.restlet.Request;
import org.restlet.Response;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.server.api.IDataProvider;

import java.util.List;

public class RoutesListRestlet extends AbstractRestlet {
    public RoutesListRestlet(@NotNull IDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    public void handle(Request request, Response response) {
        final List<Route> routes = dataProvider.getRoutes();

        packToXml(response, routes);
    }
}
