package ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.org;

import org.junit.Test;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.common.model.VehicleType;
import ru.lanjusto.busscheduler.server.api.timetable.ITimetableGetter;

/**
 * Тестируем доставалку расписания
 */
public class MgtOrgTimetableGetterTest {
    @Test
    public void simpleTest() {
        System.setProperty("http.proxyHost", "proxy.custis.ru");
        System.setProperty("http.proxyPort", "3128");

        final ITimetableGetter timetableGetter = new MgtOrgTimetableGetter();
        final Timetable timetable =  timetableGetter.get(new Route(VehicleType.TROLLEYBUS, "78"));

        System.out.println(timetable);
    }
}
