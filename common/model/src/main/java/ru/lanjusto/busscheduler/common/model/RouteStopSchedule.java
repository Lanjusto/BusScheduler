package ru.lanjusto.busscheduler.common.model;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import ru.lanjusto.busscheduler.common.utils.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Расписание остановки
 */
@Entity
@Table(name = "t_route_stop_schedule")
public class RouteStopSchedule {
    @Id
    @SequenceGenerator(name = "seq_route_stop_schedule", sequenceName = "seq_route_stop_schedule", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_route_stop_schedule")
    @Column(name = "id_route_stop_schedule")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_route_stop", nullable = false)
    private RouteStop routeStop;

    @Column(name = "day", nullable = false)
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    /**
     * массив времени отправления в виде чч:мм
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_schedule")
    private List<ScheduleTime> times = new ArrayList<ScheduleTime>();

    protected RouteStopSchedule() {
    }

    public RouteStopSchedule(RouteStop routeStop, DayOfWeek day) {
        this.routeStop = routeStop;
        this.day = day;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RouteStop getRouteStop() {
        return routeStop;
    }

    public void setRouteStop(RouteStop routeStop) {
        this.routeStop = routeStop;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public List<ScheduleTime> getTimes() {
        return times;
    }

    public void setTimes(List<ScheduleTime> times) {
        this.times = times;
    }
}
