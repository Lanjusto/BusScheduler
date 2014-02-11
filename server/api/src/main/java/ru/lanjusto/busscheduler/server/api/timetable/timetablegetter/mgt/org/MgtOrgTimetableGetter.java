package ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.org;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.server.api.browser.Browser;
import ru.lanjusto.busscheduler.server.api.timetable.ITimetableGetter;

import java.io.IOException;

/**
 * Расписания с сайта <a href="www.mosgortrans.org">www.mosgortrans.org</a>
 */
public class MgtOrgTimetableGetter implements ITimetableGetter {
    private final String URL = "http://www.mosgortrans.org/pass3";

    @NotNull
    public Timetable get(@NotNull Route route) {
        //TODO формировать адрес в зависимости от маршрута
        final String url = "http://www.mosgortrans.org/pass3/shedule.php?type=trol&way=78&date=1111100&direction=BA&waypoint=0";

        final String content;
        try {
            content = Browser.getContent(url);
            return new MgtOrgParser().extractTimetable(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}