package ru.lanjusto.busscheduler.common.model;

import ru.lanjusto.busscheduler.common.utils.Assert;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Координаты
 */
@Embeddable
public class Coordinates {
    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    @Basic(optional = true)
    private Double longitude;

    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    @Basic(optional = true)
    private Double latitude;

    protected Coordinates() {

    }

    public Coordinates(Double latitude, Double longitude) {
        Assert.notNull(latitude);
        Assert.notNull(longitude);

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return longitude + " " + latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (!latitude.equals(that.latitude)) return false;
        if (!longitude.equals(that.longitude)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = longitude.hashCode();
        result = 31 * result + latitude.hashCode();
        return result;
    }
}