package ru.lanjusto.busscheduler.server.api.restlets;

class RequestParameters {
    private final Long routeId;
    private final Long routeStopId;

    RequestParameters(Long routeId, Long routeStopId) {
        this.routeId = routeId;
        this.routeStopId = routeStopId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public Long getRouteStopId() {
        return routeStopId;
    }
}
