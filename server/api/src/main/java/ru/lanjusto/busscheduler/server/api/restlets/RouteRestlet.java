package ru.lanjusto.busscheduler.server.api.restlets;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import org.jetbrains.annotations.NotNull;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.server.api.IDataProvider;

import java.util.List;

public class RouteRestlet extends Restlet {
    private final IDataProvider dataProvider;

    public RouteRestlet(@NotNull IDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public void handle(Request request, Response response) {
        final long routeId = Long.parseLong((String) request.getAttributes().get("routeId"));
        final List<RouteStop> routeStops = dataProvider.getRouteStops(routeId);

        final XStream xStream = new XStream(new JettisonMappedXmlDriver());
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.autodetectAnnotations(true);

        response.setEntity(xStream.toXML(routeStops), MediaType.TEXT_PLAIN);
    }
}
