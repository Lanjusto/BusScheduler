package ru.lanjusto.busscheduler.server.dbupdater.service;

import ru.lanjusto.busscheduler.common.model.Coordinates;

/**
 * Created by Анечка on 02.06.2014.
 */
public class CoordinateConverter {
    /**
     * EPSG3857 используют для вывода координат Google, OpenStreetMAp
     * @param x
     * @param y
     * @return
     * @see <a href="http://wiki.gis-lab.info/w/Пересчет_координат_из_Lat/Long_в_проекцию_Меркатора_и_обратно">статья</a>
     */
    public static Coordinates fromEPSG3857 (double x, double y) {
            double a=6378137.0;
            return new Coordinates(Math.toDegrees(x / a)
                    , Math.toDegrees(2*(Math.atan(Math.pow(Math.E, y / a))-Math.PI/4)));
    }
}
