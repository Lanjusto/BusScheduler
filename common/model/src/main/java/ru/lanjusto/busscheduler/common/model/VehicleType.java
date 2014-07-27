package ru.lanjusto.busscheduler.common.model;

/**
 * Тип транспорта
 */
public enum VehicleType {
    BUS("Автобус"),

    TROLLEYBUS("Троллейбус"),

    TRAM("Трамвай"),

    MINIBUS("Маршрутное такси"),

    AQUABUS("Речной трамвай");

    private final String image;

    VehicleType(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
