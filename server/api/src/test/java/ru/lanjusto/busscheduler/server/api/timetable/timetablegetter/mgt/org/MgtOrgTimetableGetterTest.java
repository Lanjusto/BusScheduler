package ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.org;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.lanjusto.busscheduler.common.model.Direction;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.Stop;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.common.model.VehicleType;
import ru.lanjusto.busscheduler.server.api.timetable.ITimetableGetter;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailable;

import java.util.Collections;
import java.util.List;

/**
 * Тестируем доставалку расписания
 */
public class MgtOrgTimetableGetterTest {
    @BeforeClass
    public static void init() {
        System.setProperty("http.proxyHost", "proxy.custis.ru");
        System.setProperty("http.proxyPort", "3128");
    }


    @Test
    public void simpleTest01() throws NoTimetableAvailable {
        simpleTest("Савеловский вокз.");
    }

    @Test
    public void simpleTest02() throws NoTimetableAvailable {
        //  то же самое, но название остановки как у нас в базе
        simpleTest("Савёловский вокзал");
        simpleTest("Кинотеатр «Ереван»");
    }

    @Test
    public void simpleTest03() throws NoTimetableAvailable {
        //  интерполяция
        //TODO
        //simpleTest("Метро «Тимирязевская»");
    }

    private void simpleTest(String routeStopName) throws NoTimetableAvailable {
        simpleTest(routeStopName, Collections.<String>emptyList());
    }

    private void simpleTest(String routeStopName, List<String> otherRouteStops) throws NoTimetableAvailable {
        final ITimetableGetter timetableGetter = new MgtOrgTimetableGetter();
        final Route route = new Route(VehicleType.TROLLEYBUS, "78");
        final Stop stop = new Stop(routeStopName, null);

        final RouteStop routeStop = RouteStop.create(route, stop, Direction.AB);
        final Timetable timetable = timetableGetter.get(routeStop);

        System.out.println(timetable);
    }
}
