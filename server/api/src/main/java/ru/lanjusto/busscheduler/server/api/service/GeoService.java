package ru.lanjusto.busscheduler.server.api.service;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.Coordinates;

import java.math.BigDecimal;

public class GeoService {
    private final static long R = 6371; // Radius of the earth in km


    public double getDistance(@NotNull Coordinates c1, @NotNull Coordinates c2) {
        final BigDecimal dLat = deg2rad(c2.getLatitude().subtract(c1.getLatitude()));
        final BigDecimal dLon = deg2rad(c2.getLongitude().subtract(c1.getLongitude()));

        final double a = Math.sin(dLat.doubleValue() / 2) * Math.sin(dLat.doubleValue() / 2) +
                         Math.cos(deg2rad(c1.getLatitude()).doubleValue()) * Math.cos(deg2rad(c2.getLatitude()).doubleValue()) *
                         Math.sin(dLon.doubleValue() / 2) * Math.sin(dLon.doubleValue() / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    private BigDecimal deg2rad(BigDecimal deg) {
        return deg.multiply(BigDecimal.valueOf(Math.PI)).divide(BigDecimal.valueOf(100).setScale(6));
    }

}
