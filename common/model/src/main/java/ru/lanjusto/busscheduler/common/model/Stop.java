package ru.lanjusto.busscheduler.common.model;

import javax.persistence.*;

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
    private String name;

    @Basic(optional = false)
    private Coordinates coordinates;

    protected Stop() {

    }

    public Stop(String name, Coordinates coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}