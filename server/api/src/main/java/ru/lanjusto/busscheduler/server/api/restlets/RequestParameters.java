package ru.lanjusto.busscheduler.server.api.restlets;

import ru.lanjusto.busscheduler.common.model.TimetableKind;

class RequestParameters {
    private final Long routeId;
    private final Long routeStopId;
    private final TimetableKind timetableKind;

    RequestParameters(Long routeId, Long routeStopId, TimetableKind timetableKind) {
        this.routeId = routeId;
        this.routeStopId = routeStopId;
        this.timetableKind = timetableKind;
    }

    public Long getRouteId() {
        return routeId;
    }

    public Long getRouteStopId() {


        return routeStopId;
    }

    public TimetableKind getTimetableKind() {
        return timetableKind;
    }
}
