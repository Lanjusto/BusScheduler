package ru.lanjusto.busscheduler.common.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Остановка маршрута
 */
@Entity
@Table(name = "t_route_stop")
@XStreamAlias("routeStop")
public class RouteStop {
    @Id
    @SequenceGenerator(name = "seq_route_stop", sequenceName = "seq_route_stop", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_route_stop")
    @Column(name = "id_route_stop")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_stop", nullable = false)
    private Stop stop;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_route", nullable = false)
    @XStreamOmitField
    private Route route;

    @Column(name = "order_num", nullable = false)
    @Basic(optional = false)
    private int order;

    @Column(name = "direction", nullable = false)
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(name = "is_on_request", nullable = false)
    @Basic(optional = false)
    private boolean onRequest;

    @Column(name = "is_terminal", nullable = false)
    @Basic(optional = false)
    private boolean terminal;

    @Column(name = "is_mandatory", nullable = false)
    @Basic(optional = false)
    private boolean mandatory;

    //TODO время работы в скобках


    public static RouteStop create(@NotNull Route route, @NotNull Stop stop, @NotNull Direction direction, Integer order) {
        final RouteStop routeStop = new RouteStop(route, stop, direction, order);
        route.getRouteStops().add(routeStop);
        return routeStop;
    }

    protected RouteStop() {

    }

    private RouteStop(@NotNull Route route, @NotNull Stop stop, @NotNull Direction direction, int order) {
        this.route = route;
        this.stop = stop;
        this.direction = direction;
        this.order = order;
        this.mandatory = true;
    }

    public long getId() {
        return id;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getOrder() {
        return order;
    }

    public void setOnRequest(boolean isOnRequest) {
        this.onRequest = isOnRequest;
    }

    public void setTerminal(boolean isTerminal) {
        this.terminal = isTerminal;
    }

    public Stop getStop() {
        return stop;
    }

    public Route getRoute() {
        return route;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public String toString() {
        return getDirection() + ": " + getOrder() + ": " + getStop().getName() + " (" + getStop().getCoordinates() + ")";
    }
}

