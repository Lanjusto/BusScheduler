package ru.lanjusto.busscheduler.android;

import ru.lanjusto.busscheduler.common.model.Time;

public class StopScheduleItem {
    private final Time departureTime;
    private final String routeNum;
    private final String destinationPoint;

    public StopScheduleItem(Time departureTime, String routeNum, String destinationPoint) {
        this.departureTime = departureTime;
        this.routeNum = routeNum;
        this.destinationPoint = destinationPoint;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public String getRouteNum() {
        return routeNum;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }
}
