package ru.lanjusto.busscheduler.common.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Координаты
 */
@Embeddable
public class Coordinates {
    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    @Basic(optional = true)
    private BigDecimal longitude;

    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    @Basic(optional = true)
    private BigDecimal latitude;

    protected Coordinates() {

    }

    public Coordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return longitude + " " + latitude;
    }
}