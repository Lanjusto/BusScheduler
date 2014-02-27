package ru.lanjusto.busscheduler.server.api.restlets;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.server.api.IDataProvider;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailableException;

public class RoutesListRestlet extends AbstractRestlet {
    public RoutesListRestlet(@NotNull IDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    protected Object handle(RequestParameters parameters) throws NoTimetableAvailableException {
        return dataProvider.getRoutes();
    }
}
