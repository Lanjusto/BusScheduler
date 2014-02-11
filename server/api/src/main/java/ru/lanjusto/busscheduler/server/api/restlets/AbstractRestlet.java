package ru.lanjusto.busscheduler.server.api.restlets;

import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.Time;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.common.utils.CommonData;
import ru.lanjusto.busscheduler.server.api.IDataProvider;

abstract class AbstractRestlet extends Restlet {
    protected final IDataProvider dataProvider;

    protected AbstractRestlet(@NotNull IDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    protected void packToXml(@NotNull Response response, @NotNull Object object) {
        final XStream xStream = new XStream(CommonData.getXStreamDriver());
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.autodetectAnnotations(true);
        xStream.alias("Route", Route.class);
        xStream.alias("Timetable", Timetable.class);
        xStream.alias("Time", Time.class);

        response.setEntity(xStream.toXML(object), MediaType.TEXT_PLAIN);
    }

    protected Long getRouteId(@NotNull Request request) {
        return getAttributeAsLong(request, "routeId");
    }

    private Long getAttributeAsLong(@NotNull Request request, @NotNull String attributeName) {
        return Long.valueOf(getAttributeAsString(request, attributeName));
    }

    private String getAttributeAsString(@NotNull Request request, @NotNull String attributeName) {
        return (String) request.getAttributes().get("routeId");
    }
}
