package ru.lanjusto.busscheduler.common.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Маршрут
 */
@Entity
@Table(name = "t_route")
@XmlRootElement
public class Route {
    @Id
    @SequenceGenerator(name = "seq_route", sequenceName = "seq_route", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_route")
    @Column(name = "id_route")
    private long id;

    @Column(name = "vehicle_type", nullable = false)
    @Basic(optional = false)
    @Enumerated(value = EnumType.STRING)
    private VehicleType vehicleType;

    @Column(name = "num", nullable = false)
    @Basic(optional = false)
    private String num;

    @Column(name = "description", nullable = false)
    @Basic(optional = false)
    private String description;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<RouteStop> routeStops = new ArrayList<RouteStop>();

    protected Route() {

    }

    public Route(@NotNull VehicleType vehicleType, @NotNull String num) {
        this.vehicleType = vehicleType;
        this.num = num;
    }

    public long getId() {
        return id;

    }

    public String getNum() {
        return num;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RouteStop> getRouteStops() {
        return routeStops;
    }

    public List<RouteStop> getRouteStops(Direction direction) {
        final List<RouteStop> resultList = new ArrayList<RouteStop>();
        for (RouteStop routeStop : getRouteStops()) {
            if (routeStop.getDirection() == direction) {
                resultList.add(routeStop);
            }
        }

        Collections.sort(resultList, new Comparator<RouteStop>() {
            @Override
            public int compare(RouteStop o1, RouteStop o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });

        return routeStops;
    }

    @Override
    public String toString() {
        return getVehicleType() + " " + getNum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Route)) {
            return false;
        }

        Route route = (Route) o;

        if (!num.equals(route.num)) {
            return false;
        }
        if (vehicleType != route.vehicleType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = vehicleType.hashCode();
        result = 31 * result + num.hashCode();
        return result;
    }
}