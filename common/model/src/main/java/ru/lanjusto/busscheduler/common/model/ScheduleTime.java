package ru.lanjusto.busscheduler.common.model;

import javax.persistence.*;
import java.util.Date;

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

    /*
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_route_stop_schedule", nullable = false)
    private RouteStopSchedule schedule;
*/
    @Column(name = "time", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date time;

    protected ScheduleTime() {
    }

    public ScheduleTime(Date time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
