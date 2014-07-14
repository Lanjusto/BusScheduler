package ru.lanjusto.busscheduler.common.model;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Расписание маршрута
 *
 * Используется для маршрутов, для которых известна только регулярность движения.
 * Период между маршрутами часто указан как мин-макс, я пока сохраняю только максимум..
 */
@Entity
@Table(name = "t_route_schedule")
public class RouteSchedule {
    @Id
    @SequenceGenerator(name = "seq_route_schedule", sequenceName = "seq_route_schedule", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_route_schedule")
    @Column(name = "id_route_schedule")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_route", nullable = false)
    private Route route;

    @Column(name = "day", nullable = false)
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    /**
     * c какого времени чч:мм действует этот интервал
     */
    @Column(name = "actual_from")
    private String from;


    /**
     * до какого времени чч:мм действует этот интервал
     */
    @Column(name = "actual_till")
    private String till;

    /**
     * интервал следования в минутах
     */
    @Column(name = "interval")
    private Integer interval;

    /**
     * чч:мм начало работы маршрута (если в дне меняются расписания, то во всех продублировать...)
     */
    @Column(name = "day_start")
    private String dayStart;


    /**
     * чч:мм завершение работы маршрута (если в дне меняются расписания, то во всех продублировать...)
     *
     * лучше последнее отправление с первой остановки, чем прибытие в депо (а то еще обманем кого-то...)
     */
    @Column(name = "day_stop")
    private String dayStop;

    protected RouteSchedule() {
    }

    public RouteSchedule(Route route, DayOfWeek day, String from, String till, Integer interval, String dayStart, String dayStop) {
        this.route = route;
        this.day = day;
        this.from = from;
        this.till = till;
        this.interval = interval;
        this.dayStart = dayStart;
        this.dayStop = dayStop;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTill() {
        return till;
    }

    public void setTill(String till) {
        this.till = till;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getDayStart() {
        return dayStart;
    }

    public void setDayStart(String dayStart) {
        this.dayStart = dayStart;
    }

    public String getDayStop() {
        return dayStop;
    }

    public void setDayStop(String dayStop) {
        this.dayStop = dayStop;
    }
}
