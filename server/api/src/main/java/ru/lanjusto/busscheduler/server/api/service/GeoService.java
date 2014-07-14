package ru.lanjusto.busscheduler.server.api.service;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.Coordinates;

import java.math.BigDecimal;

public class GeoService {
    private final static long R = 6371; // Radius of the earth in km


    public double getDistance(@NotNull Coordinates c1, @NotNull Coordinates c2) {
        final Double dLat = deg2rad(c2.getLatitude() - c1.getLatitude());
        final Double dLon = deg2rad(c2.getLongitude() - c1.getLongitude());

        //todo разобраться
        final double a = Math.sin(dLat / 2) * Math.sin(dLat.doubleValue() / 2) +
                         Math.cos(deg2rad(c1.getLatitude())) * Math.cos(deg2rad(c2.getLatitude())) *
                         Math.sin(dLon / 2) * Math.sin(dLon / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    private Double deg2rad(Double deg) {
        return deg * Math.PI / 100;
    }

}
