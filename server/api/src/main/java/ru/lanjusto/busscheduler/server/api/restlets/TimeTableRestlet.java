package ru.lanjusto.busscheduler.server.api.restlets;

import org.jetbrains.annotations.NotNull;
import org.restlet.Request;
import org.restlet.Response;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.server.api.IDataProvider;

public class TimeTableRestlet extends AbstractRestlet {
    public TimeTableRestlet(@NotNull IDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    public void handle(Request request, Response response) {
        final long routeId = getRouteId(request);

        final Timetable timetable = dataProvider.getTimeTable(routeId);

        packToXml(response, timetable);
    }
}
