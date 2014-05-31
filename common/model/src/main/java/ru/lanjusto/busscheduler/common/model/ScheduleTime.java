package ru.lanjusto.busscheduler.common.model;

import ru.lanjusto.busscheduler.common.utils.Assert;

import javax.persistence.*;

/**
 * Строка расписания остановки
 */
@Entity
@Table(name = "t_schedule_time")
public class ScheduleTime {
    @Id
    @SequenceGenerator(name = "seq_schedule_time", sequenceName = "seq_schedule_time", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_schedule_time")
    @Column(name = "id_schedule_time")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_route_stop", nullable = false)
    private RouteStopSchedule schedule;

    //TODO возможно оптимальнее хранить в виде смещения в миллисекундах от начала дня
    @Column(name = "time", nullable = false)
    @Basic(optional = false)
    private String time;

    public ScheduleTime() {
    }

    public ScheduleTime(RouteStopSchedule schedule, String time) {
        Assert.isTrue(time.matches("/d/d:/d/d"));

        this.schedule = schedule;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RouteStopSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(RouteStopSchedule schedule) {
        this.schedule = schedule;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
