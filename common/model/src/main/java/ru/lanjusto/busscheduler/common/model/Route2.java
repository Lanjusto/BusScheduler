package ru.lanjusto.busscheduler.common.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by estroykov on 09.12.13.
 */
public class Route2 {
    @XmlAttribute
    private VehicleType s1;
    @XmlAttribute
    private String s2;

    public Route2(VehicleType s1, String s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    protected Route2() {}


    public VehicleType getS1() {
        return s1;
    }

    public String getS2() {
        return s2;
    }
}
