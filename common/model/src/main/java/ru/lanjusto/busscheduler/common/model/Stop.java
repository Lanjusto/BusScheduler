package ru.lanjusto.busscheduler.common.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Остановка
 */
@Entity
@Table(name = "t_stop")
public class Stop {
    @Id
    @SequenceGenerator(name = "seq_stop", sequenceName = "seq_stop", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_stop")
    @Column(name = "id_stop")
    private long id;

    @Column(name = "name", nullable = false)
    @Basic(optional = false)
    //TODO индекс
    private String name;

    @Basic(optional = false)
    private Coordinates coordinates;


    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<RouteStop> routeStops = new ArrayList<RouteStop>();

    @Column(name = "city", nullable = false)
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private City city;

    @Column(name = "dtm_update", nullable = false)
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /***
     * Источник получения данных о маршруте. Нужно чтобы 2 источника не "перекрывались", ну и для истории
     */
    @Column(name = "source", nullable = false)
    @Basic(optional = false)
    private String source;


    /***
     * идентификатор в системе источнике, иногда полезно
     */
    @Column(name = "source_id")
    @Basic
    //TODO индекс
    private String sourceId;

    protected Stop() {

    }

    public Stop(String name, Coordinates coordinates, @NotNull City city, @NotNull Date updateTime, @NotNull String source) {
        this.name = name;
        this.coordinates = coordinates;
        this.city = city;
        this.updateTime = updateTime;
        this.source = source;
    }

    public Stop(String name, Coordinates coordinates, @NotNull City city, @NotNull Date updateTime, @NotNull String source, String sourceId) {
        this.name = name;
        this.coordinates = coordinates;
        this.city = city;
        this.updateTime = updateTime;
        this.source = source;
        this.sourceId = sourceId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}