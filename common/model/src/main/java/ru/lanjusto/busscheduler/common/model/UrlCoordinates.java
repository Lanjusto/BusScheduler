package ru.lanjusto.busscheduler.common.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Связь URL с координатами
 */
@Entity
@Table(name = "t_url_coordinates")
public class UrlCoordinates {
    @Id
    @SequenceGenerator(name = "seq_url_coordinates", sequenceName = "seq_url_coordinates", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_url_coordinates")
    @Column(name = "id_url_coordinates")
    private long id;

    @Column(name = "url", nullable = false)
    @Basic(optional = false)
    private String url;

    @Basic(optional = false)
    private Coordinates coordinates;

    protected UrlCoordinates() {

    }

    public UrlCoordinates(@NotNull String url, @NotNull Coordinates coordinates) {
        this.url = url;
        this.coordinates = coordinates;
    }

    public String getUrl() {
        return url;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
