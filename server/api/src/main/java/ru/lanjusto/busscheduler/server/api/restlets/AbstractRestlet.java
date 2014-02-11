package ru.lanjusto.busscheduler.server.api.restlets;

import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.Time;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.common.utils.CommonData;
import ru.lanjusto.busscheduler.server.api.IDataProvider;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailable;

abstract class AbstractRestlet extends Restlet {
    protected final IDataProvider dataProvider;

    protected AbstractRestlet(@NotNull IDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    protected abstract Object handle(RequestParameters requestParameters) throws NoTimetableAvailable;

    @Override
    public final void handle(Request request, Response response) {
        final Long routeId = getAttributeAsLong(request, "routeId");
        final Long routeStopId = getAttributeAsLong(request, "routeStopId");
        final RequestParameters parameters = new RequestParameters(routeId, routeStopId);

        final Object result;
        try {
            result = handle(parameters);
            packToXml(response, result);
        } catch (NoTimetableAvailable e) {
            response.setStatus(new Status(Status.CLIENT_ERROR_NOT_FOUND, e.getClass().getName()));
        }
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

    @Nullable
    private Long getAttributeAsLong(@NotNull Request request, @NotNull String attributeName) {
        final String value = getAttributeAsString(request, attributeName);
        return value == null ? null : Long.valueOf(value);
    }

    @Nullable
    private String getAttributeAsString(@NotNull Request request, @NotNull String attributeName) {
        return (String) request.getAttributes().get(attributeName);
    }
}
