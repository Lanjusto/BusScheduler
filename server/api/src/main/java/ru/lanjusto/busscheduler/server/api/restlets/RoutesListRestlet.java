package ru.lanjusto.busscheduler.server.api.restlets;

import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.utils.CommonData;
import ru.lanjusto.busscheduler.server.api.IDataProvider;

import java.util.List;

public class RoutesListRestlet extends Restlet {
    private final IDataProvider dataProvider;

    public RoutesListRestlet(@NotNull IDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public void handle(Request request, Response response) {
        final List<Route> routes = dataProvider.getRoutes();

        final XStream xStream = new XStream(CommonData.getXStreamDriver());
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.alias("Route", Route.class);

        response.setEntity(xStream.toXML(routes), MediaType.TEXT_PLAIN);
    }
}
