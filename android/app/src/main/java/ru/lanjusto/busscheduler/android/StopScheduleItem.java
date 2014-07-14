package ru.lanjusto.busscheduler.android;

import ru.lanjusto.busscheduler.common.model.Time;

public class StopScheduleItem {
    private final Time departureTime;
    private final String restingTime;
    private final String routeNum;
    private final String destinationPoint;

    public StopScheduleItem(Time departureTime, String restingTime, String routeNum, String destinationPoint) {
        this.departureTime = departureTime;
        this.restingTime = restingTime;
        this.routeNum = routeNum;
        this.destinationPoint = destinationPoint;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public String getRestingTime() {
        return restingTime;
    }

    public String getRouteNum() {
        return routeNum;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }
}
