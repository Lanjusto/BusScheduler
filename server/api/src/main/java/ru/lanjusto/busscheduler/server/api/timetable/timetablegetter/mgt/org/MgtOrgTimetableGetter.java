package ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.org;

import org.jetbrains.annotations.NotNull;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanjusto.busscheduler.common.model.Coordinates;
import ru.lanjusto.busscheduler.common.model.Direction;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.common.utils.Assert;
import ru.lanjusto.busscheduler.server.api.browser.Browser;
import ru.lanjusto.busscheduler.server.api.browser.HtmlParser;
import ru.lanjusto.busscheduler.server.api.service.GeoService;
import ru.lanjusto.busscheduler.server.api.service.RouteService;
import ru.lanjusto.busscheduler.server.api.service.TimetableService;
import ru.lanjusto.busscheduler.server.api.timetable.ITimetableGetter;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailableException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Расписания с сайта <a href="www.mosgortrans.org">www.mosgortrans.org</a>
 */
public class MgtOrgTimetableGetter implements ITimetableGetter {
    private final String URL = "http://www.mosgortrans.org/pass3";
    final Logger log = LoggerFactory.getLogger(MgtOrgTimetableGetter.class);

    @NotNull
    public Timetable get(@NotNull RouteStop routeStop) throws NoTimetableAvailableException {
        final String url = getUrl(routeStop);
        log.info("Getting timetable for {} on {}.", routeStop, url);

        final String content;
        try {
            content = Browser.getContent(url);

            if (content.contains("Не удалось открыть файл ресурсов. Расписание недоступно")) {
                throw new NoTimetableAvailableException();
            }

            final Map<String, String> map = getMap(content);
            try {
                final String routeStopTimetableBlock = getTimetableBlock(map, routeStop.getStop().getName());
                return getTimetable(routeStopTimetableBlock);
            } catch (NoRouteStopFound e) {
                // Расписания по остановке нет. Делаем интерполяцию.
                return makeInterpolation(map, routeStop);
            }
        } catch (IOException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    private String getUrl(@NotNull RouteStop routeStop) {
        final String routeType;
        final Route route = routeStop.getRoute();
        switch (route.getVehicleType()) {
            case BUS:
                routeType = "avto";
                break;
            case TROLLEYBUS:
                routeType = "trol";
                break;
            case TRAM:
                routeType = "tram";
                break;
            default:
                throw new IllegalArgumentException();
        }
        final Direction direction = routeStop.getDirection();

        return MessageFormat.format("{0}/shedule.php?type={1}&way={2}&date={3}&direction={4}&waypoint={5}",
                                    URL,
                                    routeType,
                                    route.getNum(),
                                    "1111100",
                                    direction,
                                    "all");
    }

    /**
     * Принимая на вход html-страницу с расписаниями по нескольким остановкам, бьёт её на части, ставя в соответствие
     * имени остановки расписание по ней
     *
     * @param content расписания по всем остановкам
     * @return хэш, в котором названию остановки ставится в соответствие расписание по этой остановке
     */
    @NotNull
    private Map<String, String> getMap(@NotNull String content) {
        final Map<String, String> map = new HashMap<String, String>();

        // теперь в тегах h2 названия остановок, а в тегах table — расписание
        final Elements tableElements = HtmlParser.getElements(content, "table");
        final Elements h2Elements = HtmlParser.getElements(content, "h2");

        // есть лишний h2 в подвале («обратие внимание») и четыре лишних table-а в заголовке
        Assert.isTrue(h2Elements.size() - 1 == tableElements.size() - 4);

        for (int i = 0; i < h2Elements.size() - 1; i++) {
            final String routeStopName = h2Elements.get(i).text();
            final String timetableBlock = tableElements.get(i + 3).html();

            map.put(routeStopName, timetableBlock);
        }

        return map;
    }

    @NotNull
    private String getTimetableBlock(@NotNull Map<String, String> map, @NotNull String routeStopName) throws NoRouteStopFound {
        for (String mapKey : map.keySet()) {
            if (equal(mapKey, routeStopName)) {
                return map.get(mapKey);
            }
        }
        throw new NoRouteStopFound();
    }

    private boolean equal(@NotNull String s1, @NotNull String s2) {
        s1 = normalize(s1);
        s2 = normalize(s2);
        return s1.equals(s2);
    }

    @NotNull
    private String normalize(@NotNull String s) {
        s = s.toLowerCase();
        s = s.replace("ё", "е");
        s = s.replace("ул.", "улица");
        s = s.replace("ш.", "шоссе");
        s = s.replace("к/т", "кинотеатр");
        s = s.replace("«", "\"");
        s = s.replace("»", "\"");
        s = s.replace("вокз.", "вокзал");
        s = s.replace("пер.", "переулок");
        return s;
    }

    private Timetable makeInterpolation(@NotNull Map<String, String> map, @NotNull RouteStop routeStop) {
        final RouteService routeService = new RouteService();

        RouteStop previousRouteStop = routeStop;
        RouteStop nextRouteStop = routeStop;

        String timetableBlock1 = null;
        String timetableBlock2 = null;

        while (routeService.getPrevious(previousRouteStop) != null) {
            previousRouteStop = routeService.getPrevious(previousRouteStop);
            try {
                timetableBlock1 = getTimetableBlock(map, previousRouteStop.getStop().getName());
                break;
            } catch (NoRouteStopFound ignore) {

            }
        }

        while (routeService.getNext(nextRouteStop) != null) {
            nextRouteStop = routeService.getNext(nextRouteStop);
            try {
                timetableBlock2 = getTimetableBlock(map, nextRouteStop.getStop().getName());
                break;
            } catch (NoRouteStopFound ignore) {

            }
        }

        if (timetableBlock1 == null || timetableBlock2 == null) {
            //TODO
            throw new UnsupportedOperationException();
        } else {

            final Coordinates c0 = routeStop.getStop().getCoordinates();
            final Coordinates c1 = previousRouteStop.getStop().getCoordinates();
            final Coordinates c2 = nextRouteStop.getStop().getCoordinates();

            log.info("Interpolation timetable in {}", routeStop);
            log.info("Next routeStop is {}", nextRouteStop);
            log.info("Previous routeStop is {}", previousRouteStop);

            final GeoService geoService = new GeoService();

            final double d1 = geoService.getDistance(c0, c1);
            final double d2 = geoService.getDistance(c0, c2);

            log.info("Distances are {} and {}.", d1, d2);

            final Timetable timetable1 = getTimetable(timetableBlock1);
            final Timetable timetable2 = getTimetable(timetableBlock2);

            return TimetableService.interpolate(timetable1, timetable2, d1 / d2);
        }
    }

    private Timetable getTimetable(@NotNull String routeStopTimetableBlock) {
        return new MgtOrgParser().extractTimetable(routeStopTimetableBlock);
    }
}