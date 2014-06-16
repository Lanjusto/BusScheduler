package ru.lanjusto.busscheduler.common.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

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

    @Column(name = "city", nullable = false)
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private City city;

    @Column(name = "dtm_update")
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /***
     * Источник получения данных о маршруте. Нужно чтобы 2 источника не "перекрывались", ну и для истории
     */
    @Column(name = "source", nullable = false)
    @Basic(optional = false)
    private String source;

    @Column(name = "source_id", nullable = true)
    @Basic(optional = true)
    private String sourceId;


    protected Route() {

    }

    public Route(VehicleType vehicleType, String num, City city, String source) {
        this.vehicleType = vehicleType;
        this.num = num;
        this.city = city;
        this.source = source;
        this.updateTime = updateTime;
    }

    public Route(@NotNull VehicleType vehicleType, @NotNull String num, @NotNull String description, @NotNull City city, @NotNull String source, String sourceId) {
        this.vehicleType = vehicleType;
        this.num = num;
        this.city = city;
        this.updateTime = updateTime;
        this.source = source;
        this.description = description;
        this.sourceId = sourceId;
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

        return resultList;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
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

        if (id == route.getId()) {
            return true; //todo не уверен, что это всегда верно... Женя, посмотри
        }

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