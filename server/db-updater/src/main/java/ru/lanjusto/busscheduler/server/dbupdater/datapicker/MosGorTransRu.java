package ru.lanjusto.busscheduler.server.dbupdater.datapicker;

import com.google.inject.Provider;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanjusto.busscheduler.common.model.*;
import ru.lanjusto.busscheduler.common.utils.Assert;
import ru.lanjusto.busscheduler.server.dbupdater.browser.Browser;
import ru.lanjusto.busscheduler.server.dbupdater.service.RouteMergeService;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Маршруты на одной странице, около 300
 * http://www.mosgortrans.ru/routes/routes/
 *
 * Далее идет страница с вариантами расписания по дням
 * http://www.mosgortrans.ru/rasp/1/1000940/index.html
 *
 * потом список остановок на данный вид "дня"
 * http://www.mosgortrans.ru/rasp/1/1000940/48094/index.html
 *
 * И потом только страница с расписанием.
 * http://www.mosgortrans.ru/rasp/1/1000940/48094/3764/index.html
 *
 * Есть несколько плохих случаев когда в определенное время маршрут не полный, только до какой-то остановки, я на них забил.
 *
 * Идентификатор остановки вытащить не удалось, т.к. даже для одного маршрута остановка может иметь разные идентификаторы в разных днях (редкость но есть)
 */
public class MosGorTransRu implements IDataPicker {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Provider<EntityManager> em;
    private final RouteMergeService routeMergeService;

    private final String source = "MosGorTrans";
    private final City city = City.MOSCOW;

    public MosGorTransRu(Provider<EntityManager> em, RouteMergeService routeMergeService) {
        this.em = em;
        this.routeMergeService = routeMergeService;
    }

    @Override
    public void pickData(Date expireDate) throws IOException, ParseException {
        em.get().getTransaction().begin();

        getRoutes(expireDate);
    }

    private List<Route> getRoutes(Date expireDate) throws IOException {
        List<Route> routes = new ArrayList<>();

        final String answer = Browser.getContent("http://www.mosgortrans.ru/rasp/index.html", Charset.forName("UTF8"));
        Document document = Jsoup.parseBodyFragment(answer);
        Elements elements = document.getElementsByTag("a");
        for (Element element : elements) {
            String num = element.ownText();
            String url = element.attr("href").substring(1).replace("/index.html","");
            VehicleType type = parseVehicleType(url);

            //todo заполнение описания маршрута
            Route route = routeMergeService.getRoute(city, num, type, source, " ", false);

            if (route.getUpdateTime() == null || expireDate.compareTo(route.getUpdateTime()) > 0) {
                routeMergeService.clearRoute(route);
                importRoute(route, url);
            }

            route.setUpdateTime(new Date());
            em.get().getTransaction().commit();
            em.get().clear(); //иначе контекст вырастает до огромных размеров
            em.get().getTransaction().begin();


        }

        return routes;
    }

    private void importRoute(Route route, String url) throws IOException {
        log.info(route.toString());
        final String answer = Browser.getContent("http://www.mosgortrans.ru/rasp"+url+"/index.html", Charset.forName("UTF8"));
        Document document = Jsoup.parseBodyFragment(answer);

        // для начала достаем остановки маршрута
        Elements elements = document.getElementsByTag("a");
        String stopsUrl = elements.last().attr("href").substring(1).replace("/index.html", "");

        Elements trs = document.getElementsByTag("tr");
        int row = 0;
        boolean first = true;
        for (Element tr : trs) {
            if (row++ < 2) {
                continue;
            }
            Elements tds = tr.getElementsByTag("td");
            DayOfWeek dayOfWeek = parseDay(tds.first().text());
            String dayUrl = tds.last().getElementsByTag("a").first().attr("href");
            dayUrl = dayUrl.substring(1).replace("/index.html","");

            importDaySchedules(route, dayOfWeek, url + dayUrl, first);
            first = false;
        }
    }

    private void importDaySchedules(Route route, DayOfWeek dayOfWeek, String dayUrl, boolean first) throws IOException {
        final String answer = Browser.getContent("http://www.mosgortrans.ru/rasp" + dayUrl + "/index.html", Charset.forName("UTF8"));

        extractRouteStops(route, answer, Direction.AB, dayOfWeek, first, dayUrl);
        extractRouteStops(route, answer, Direction.BA, dayOfWeek, first, dayUrl);
    }

    private void importRouteStopSchedule(RouteStopSchedule schedule, String url) throws IOException {
        final String answer = Browser.getContent("http://www.mosgortrans.ru/rasp/"+url, Charset.forName("UTF8"));
        Document document = Jsoup.parseBodyFragment(answer);

        final Elements elements = document.getElementsByTag("td");
        int hour = -1;
        int minute;
        for (Element element : elements) {
            final String attributeValue = element.attr("class");
            final String content = element.text();
            if (content.isEmpty()) {
                continue;
            } else if (attributeValue.startsWith("hour")) {
                hour = Integer.valueOf(content);
            } else if (attributeValue.startsWith("minute")) {
                if (content.equals(String.valueOf((char)160))) {
                    continue;
                }
                for (String s : content.split(" ")) {
                    // иногда в скобках указывают примечание
                    int i = s.indexOf("(");
                    if (i!=-1) {
                        s = s.substring(0, i);
                    }
                    minute = Integer.valueOf(s);
                    schedule.getTimes().add(new ScheduleTime(new Date((hour*60+minute)*60_000)));
                }
            } else {
                Assert.fail();
            }
        }


    }

    private DayOfWeek parseDay(String text) {
        switch (text) {
            case "БУДНИ": return DayOfWeek.MON_FRI;
            case "ВЫХОДНЫЕ": return DayOfWeek.SAT_SUN;
            case "БУДНИ, ВЫХОДНЫЕ": return DayOfWeek.ALL;
            case "ПТ;СБ;": return DayOfWeek.FRI_SAT;
            case "ПН;ВТ;СР;ЧТ": return DayOfWeek.MON_THU;
            case "ПТ": return DayOfWeek.FRIDAY;
            case "СБ": return DayOfWeek.SATURDAY;
            case "ВС": return DayOfWeek.SUNDAY;
            default: throw new RuntimeException("неизвестный день "+text);
        }
    }

    private void extractRouteStops(Route route, String answer, Direction direction, DayOfWeek day, boolean first, String dayUrl) throws IOException {
        int i=0,j;
        int ind = 0;
        String pref = "markers" + (direction.equals(Direction.AB) ? 1 : 2) + "[";
        //markers1[0] = createMarker(map,new google.maps.LatLng("55.666906", "37.769237"), "<B><a style=color:blue href=./10794/index.html>Ул. Перерва, 68</a></B><BR><BR>до конечн. пункта <B>Метро &quot;Печатники&quot;</B><BR>№ п/п: <b>1</b>", 1);
        while ((i=answer.indexOf(pref, i+1)) != -1) {
            j = answer.indexOf(");", i+1);
            String str = answer.substring(i,j);
            int i1 = str.indexOf("\"");
            int i2 = str.indexOf("\"", i1+1);
            int i3 = str.indexOf("\"", i2+1);
            int i4 = str.indexOf("\"", i3+1);
            int i5 = str.indexOf("\"", i4+1);
            int i6 = str.indexOf("\"", i5+1);
            double lat = Double.valueOf(str.substring(i1 + 1, i2));
            double lon = Double.valueOf(str.substring(i3+1, i4));

            String stopFrag = str.substring(i5+1, i6);
            Document document = Jsoup.parseBodyFragment(stopFrag);
            Element a = document.getElementsByTag("a").first();

            String name = a.text();
            String schedUrl = dayUrl + a.attr("href").substring(1);

            Stop stop = routeMergeService.mergeStop(name, city, source, new Coordinates(lat, lon));

            RouteStop routeStop = insertIntoRoute(route, stop, direction, ind, first);
            ind = routeStop.getOrder()+1;

            RouteStopSchedule schedule = new RouteStopSchedule(routeStop, day);
            em.get().persist(schedule);

            importRouteStopSchedule(schedule, schedUrl);
        }

        //хвост помечаем неиспользуемым
        List<RouteStop> routeStops = route.getRouteStops(direction);
        for (i=ind; i< routeStops.size();i++) {
            routeStops.get(i).setMandatory(false);
        }
    }

    private RouteStop insertIntoRoute(Route route, Stop stop, Direction direction, int index, boolean firstDay) {
        // функция объединяет маршруты разных дней, возвращает номер следующей остановки в маршруте
        if (firstDay) {
            // это первый день по маршруту, тут сравнивать нечего
            RouteStop routeStop = RouteStop.create(route, stop, direction, index);
            em.get().persist(routeStop);
            return routeStop;
        }

        List<RouteStop> routeStops = route.getRouteStops(direction);

        if (routeStops.size() < index+1) {
            // за этот день маршрут длиннее
            RouteStop routeStop = RouteStop.create(route, stop, direction, index);
            routeStop.setMandatory(false);
            em.get().persist(routeStop);
            return routeStop;
        }

        //  В хорошем случае порядок остановок одинаковый, в плохом могут быть несовпадения
        if (stopsEqual(routeStops.get(index).getStop(), stop)) {
            return routeStops.get(index);
        }

        // несовпадение, нужно понять тут пропущена остановки или вставлена новая
        for (int i=index; i<routeStops.size();i++) {
            if (stopsEqual(routeStops.get(i).getStop(), stop)) {
                // пропущены предшествующие остановки
                for (int j=index; j<i; j++) {
                    routeStops.get(j).setMandatory(false);
                }
                return routeStops.get(i);
            }
        }

        // вставляем новую
        for (int i=index; i<routeStops.size();i++) {
            RouteStop routeStop = routeStops.get(i);
            routeStop.setOrder(routeStop.getOrder() + 1);
        }
        RouteStop routeStop = RouteStop.create(route, stop, direction, index);
        routeStop.setMandatory(false);
        em.get().persist(routeStop);
        return routeStop;
    }

    private boolean stopsEqual(Stop stop1, Stop stop2) {
        if (! stop1.getName().equals(stop2.getName())) {
            return false;
        }

        return stop1.getCoordinates().equals(stop2.getCoordinates());
    }

    private VehicleType parseVehicleType(String url) {
        switch (url.substring(1,2)) {
            case "1": return VehicleType.BUS;
            case "3": return VehicleType.TRAM;
            default: throw new RuntimeException("Неизвестный вид транспорта "+url.substring(2,3));
        }
    }
}
