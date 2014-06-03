package ru.lanjusto.busscheduler.server.dbupdater.datapicker;

import com.google.inject.Provider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanjusto.busscheduler.common.model.*;
import ru.lanjusto.busscheduler.server.dbupdater.browser.Browser;
import ru.lanjusto.busscheduler.server.dbupdater.service.CoordinateConverter;
import ru.lanjusto.busscheduler.server.dbupdater.service.RouteMergeService;

import javax.persistence.EntityManager;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Date;

/**
 * Created by Анечка on 27.05.2014.
 */
public class SPBPicker implements IDataPicker {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Provider<EntityManager> em;
    private final RouteMergeService routeMergeService;

    private final String source = "SPB";
    private final City city = City.PETERBURG;

    public SPBPicker(Provider<EntityManager> em, RouteMergeService routeMergeService) {
        this.em = em;
        this.routeMergeService = routeMergeService;
    }

    @Override
    /***
     * Описание извлекаемых данных
     *
     * Маршруты с номерами и видами транспорта
     * Остановки (координаты остановки одни независимо от направления)
     *
     * Расписание. Бывает самым разным. Интервалы движения в определенные часы, точное расписание, может не быть вообще.
     */
    public void pickData(Date expireDate) throws IOException, ParseException {
        em.get().getTransaction().begin();
        Map<Long, Route> routes = getRoutes();

        for (Long routeId : routes.keySet()) {
            Route route = routes.get(routeId);

            if (route.getUpdateTime() == null || expireDate.compareTo(route.getUpdateTime()) > 0) {
                log.info(route.toString());
                importRoute(routeId, route);

                route.setUpdateTime(new Date());
                em.get().getTransaction().commit();
                em.get().getTransaction().begin();
            }

        }
        em.get().getTransaction().commit();

        // удаляем остановки без связанных маршрутов
        // удаляем так и не обновившиеся маршруты (если дошли то их не было в списке маршрутов на сайте)
    }

    private void importRoute(Long routeId, Route route) throws IOException, ParseException {
        routeMergeService.clearRoute(route);

        // достаем остановки (название - координаты)
        Map<String, Stop> stopes = getStopes(routeId);

        // достаем маршрут по остановкам
        List<RouteStop> routeStops = importRouteStops(routeId, route, stopes);

        // достаем расписание по остановкам
        for (RouteStop routeStop : routeStops) {
            boolean cont = importSchedule(routeStop, routeId);
            if (!cont) {
                break;
            }
        }
    }

    private List<RouteStop> importRouteStops(Long routeId, Route route, Map<String, Stop> stopes) throws IOException {
        List<RouteStop> routeStops = new ArrayList<>();

        final String answer = Browser.getContent("http://transport.orgp.spb.ru/Portal/transport/route/" + routeId + "/schedule", Charset.forName("UTF8"));
        Document document = Jsoup.parseBodyFragment(answer);

        extractStopRoutes(route, stopes, routeStops, document.getElementById("direct-stops"), Direction.AB);
        Element element = document.getElementById("return-stops");
        if (element != null) {
            extractStopRoutes(route, stopes, routeStops, element, Direction.BA);
        }

        for (RouteStop routeStop : routeStops) {
            em.get().persist(routeStop);
        }

        return routeStops;
    }

    private void extractStopRoutes(Route route, Map<String, Stop> stopes, List<RouteStop> routeStops, Element root, Direction direction) {
        int i = 0;
        final Elements elements = root.getElementsByTag("li");
        for (Element element : elements) {
            String stopName;
            final Elements aElements = element.getElementsByTag("a");
            String url = null;
            if (aElements.size() == 0) {
                //это выделенная по умолчанию остановка...
                stopName = element.ownText();
            } else {
                stopName = aElements.get(0).ownText();
                url = aElements.get(0).attr("href");
            }
            stopName = stopName.replaceAll("  ", " ");

            if (stopes.get(stopName) == null) {
                String id = url.substring(url.lastIndexOf("/", 2), url.lastIndexOf("/"));
                //id можно теоретически взять из href
                log.warn("Источник не определил координаты остановки "+stopName+", включается ручное определение координат");
                switch (stopName) {
                    case "ОБОРОТНАЯ СТАНЦИЯ СТАНЦИЯ МЕТРО\"ЛОМОНОСОВСКАЯ\"": stopName = "оборотная станция метро Ломоносовская"; break;
                    case "Б. САМПСОНИЕВСКИЙ ПР. (ПОСАДКА)":
                        manualAddStop(stopes, stopName, 59.984293, 30.33669, id);
                        break;
                    case "КОНЕЧНАЯ СТАНЦИЯ «СУЗДАЛЬСКИЙ ПР.»":
                        manualAddStop(stopes, stopName, 60.046439, 30.406476, id);
                        break;
                    case "ОБОРОТНАЯ СТАНЦИЯ \"СТАНЦИЯ МЕТРО \"ВЫБОРГСКАЯ\"":
                        manualAddStop(stopes, stopName, 59.971307, 30.348186, id);
                        break;
                    case "КАНОНЕРСКИЙ ОСТРОВ":
                        manualAddStop(stopes, stopName, 59.904846, 30.21673, id);
                        break;
                    case "ЗАЯЧИЙ РЕМИЗ":
                        Stop stop = stopes.get("ЗАЙЧИЙ РЕМИЗ");
                        stop.setName("ЗАЯЧИЙ РЕМИЗ");
                        stopName = "ЗАЙЧИЙ РЕМИЗ";
                        break;
                    case "Б. СМОЛЕНСКИЙ ПР.,УГ. УЛ. БАБУШКИНА":
                        manualAddStop(stopes, "БОЛЬШОЙ СМОЛЕНСКИЙ ПР.", 59.898721, 30.419236, id);
                        stopName = "БОЛЬШОЙ СМОЛЕНСКИЙ ПР.";
                        break;
                    case "ПЛ. РАСТРЕЛЛИ (ПОСАДКА)":
                        manualAddStop(stopes, stopName, 59.948679, 30.389262, id);
                        break;
                    case "РОЩИНСКАЯ УЛ.":
                        manualAddStop(stopes, stopName, 59.883742, 30.319455, id);
                        break;
                    default: manualAddStop(stopes, stopName, 0, 0, id);//throw new RuntimeException("Не определены координаты для остановки " + stopName);
                }

            }
            routeStops.add(RouteStop.create(route, stopes.get(stopName), direction, i++));
        }
    }

    private void manualAddStop(Map<String, Stop> stopes, String stopName, double latitude, double longitude, String id) {
        Stop stop = new Stop(stopName, new Coordinates(latitude, longitude), City.PETERBURG, new Date(), source, id);
        em.get().persist(stop);
        stopes.put(stopName, stop);
    }

    private Map<String, Stop> getStopes(Long routeId) throws IOException, ParseException {
        Map<String, Stop> map = new HashMap<>();

        String answer = Browser.getContent("http://transport.orgp.spb.ru/Portal/transport/map/poi?ROUTE="+ routeId +"&REQUEST=GetFeature&_=1401302330057", Charset.forName("UTF-8"));

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(answer);

        JSONArray stops = (JSONArray) json.get("features");
        Iterator<JSONObject> stopsIterator = stops.iterator();
        while (stopsIterator.hasNext()) {
            JSONObject jsonStop = stopsIterator.next();
            JSONObject jsonStopProp = (JSONObject) jsonStop.get("properties");
            String name = ((String) jsonStopProp.get("name")).replaceAll("[ ]+", " ").trim();
            String id = ((Long) jsonStopProp.get("id")).toString();
            JSONArray coord = (JSONArray) ((JSONObject) jsonStop.get("geometry")).get("coordinates");
            Coordinates coordinates = CoordinateConverter.fromEPSG3857((Double) coord.get(0), (Double) coord.get(1));
            Stop stop = routeMergeService.mergeStop(name, city, source, coordinates, id);
            map.put(name, stop);
        }

        return map;
    }

    private Map<Long, Route> getRoutes() throws IOException, ParseException {
        Map<Long, Route> map = new HashMap<>();

        Long totalRoutes = 1L; //не важно, переопределится при первом запросе
        Long displayStart = 0L;
        Integer window = 50;
        while (displayStart < totalRoutes) {
            final URL urlUrl = new URL("http://transport.orgp.spb.ru/Portal/transport/routes/list");
            final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();
            urlConnection.setRequestMethod("POST");

            urlConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes("sEcho=8&iColumns=11&sColumns=id%2CtransportType%2CrouteNumber%2Cname%2Curban%2CpoiStart%2CpoiFinish%2Ccost%2CforDisabled%2CscheduleLinkColumn%2CmapLinkColumn"
                    + "&iDisplayStart=" + displayStart + "&iDisplayLength=" + window +
                    "&sNames=id%2CtransportType%2CrouteNumber%2Cname%2Curban%2CpoiStart%2CpoiFinish%2Ccost%2CforDisabled%2CscheduleLinkColumn%2CmapLinkColumn&iSortingCols=1&iSortCol_0=2&sSortDir_0=asc&bSortable_0=true&bSortable_1=true&bSortable_2=true&bSortable_3=true&bSortable_4=true&bSortable_5=true&bSortable_6=true&bSortable_7=true&bSortable_8=true&bSortable_9=false&bSortable_10=false&transport-type=0&transport-type=46&transport-type=2&transport-type=1");
            wr.flush();
            wr.close();

            final String answer = Browser.getAnswer(urlConnection);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(answer);

            totalRoutes = (Long) json.get("iTotalRecords");
            JSONArray routesJson = (JSONArray) json.get("aaData");
            Iterator<JSONArray> routesIterator = routesJson.iterator();
            while (routesIterator.hasNext()) {
                JSONArray jsonRoute = routesIterator.next();
                Long id = (Long) jsonRoute.get(0);
                String type = (String) ((JSONObject) jsonRoute.get(1)).get("systemName");
                VehicleType vehicleType;
                switch (type) {
                    case "tram":
                        vehicleType = VehicleType.TRAM;
                        break;
                    case "bus":
                        vehicleType = VehicleType.BUS;
                        break;
                    case "trolley":
                        vehicleType = VehicleType.TROLLEYBUS;
                        break;
                    case "ship":
                        vehicleType = VehicleType.AQUABUS;
                        break;
                    default:
                        throw new RuntimeException("неизвестный вид телеги " + type);
                }
                String num = (String) jsonRoute.get(2);
                String desc = (String) jsonRoute.get(3);
                if(jsonRoute.get(7) != null) {
                    BigDecimal price = new BigDecimal((Double) jsonRoute.get(7)); //todo определиться нужна ли цена
                }

                Route route = routeMergeService.getRoute(City.PETERBURG, num, vehicleType, source, desc, false);
                map.put(id, route);
            }

            displayStart += window;
        }

        return map;
    }


    /**
     * возвращает флаг необходимости смотреть расписание по другим остановкам направления
     *
     * это не нужно если расписание интервальное
     * @param routeStop
     * @param routeId
     * @return
     * @throws IOException
     */
    private boolean importSchedule(RouteStop routeStop, Long routeId) throws IOException {
        String dir = routeStop.getDirection().equals(Direction.AB) ? "/direct" : "/return";
        final String answer = Browser.getContent("http://transport.orgp.spb.ru/Portal/transport/route/"+ routeId +"/schedule/"+ routeStop.getStop().getSourceId() + dir, Charset.forName("UTF-8"));

        Document document = Jsoup.parseBodyFragment(answer);
        Element scheduleTable = document.getElementById("scheduleTable");
        if (scheduleTable == null) {
            // нет расписания!
            return false;
        }
        Elements rows =  scheduleTable.getElementsByTag("tr");

        if (rows.get(0).getElementsByTag("th").get(0).text().contains("Период дня"))
        {
            Map<DayOfWeek, String[]> dayStartStop = importDayStartStop(document);
            importRouteSchedule(routeStop, rows, dayStartStop);
            return false;
        }
        else if (rows.get(0).getElementsByTag("th").get(0).ownText().contains("Час")) {
            importStopSchedule(routeStop, rows);
            return true;
        } else {
            throw new RuntimeException("unknown schedule type "+ rows.get(0).getElementsByTag("span").get(0).ownText());
        }


    }

    private void importStopSchedule(RouteStop routeStop, Elements rows) {

        int rowInd = 0;
        List<RouteStopSchedule> schedules = new ArrayList<>();
        for(Element row: rows) {
            if (rowInd == 0) {
                boolean skip = true;
                for (Element th : row.getElementsByTag("th")) {
                    if (skip) {
                        skip = false;
                    } else {
                        RouteStopSchedule e = new RouteStopSchedule(routeStop, parseDay(th.ownText()));
                        em.get().persist(e);
                        schedules.add(e);
                    }
                }
            } else {
                int sched = -1;
                for (Element td : row.getElementsByTag("td")) {
                    long hour = 0;
                    if (sched == -1) {
                        hour = Long.valueOf(td.text().substring(0,2).trim());
                    } else {
                        String[] minutes = td.ownText().split(",");
                        for (String minute : minutes) {
                            if (!minute.trim().equals("")) {
                                //todo 00:30 относится к СЛЕДУЮЩЕМУ дню, видимо так как это заканчивается смена предшествующего дня
                                Date time = new Date((hour * 60 + Long.valueOf(minute.trim())) * 60_000);
                                schedules.get(sched).getTimes().add(new ScheduleTime(time));
                            }
                        }
                    }
                    sched++;
                }
            }
            rowInd++;
        }

    }

    private Map<DayOfWeek, String[]> importDayStartStop(Document document) {
        Map<DayOfWeek, String[]> map = new HashMap<>();

        Element table = document.getElementById("movementTimeTable");

        int rowInd = 0;
        List<DayOfWeek> days = new ArrayList<>();
        List<String> starts = new ArrayList<>();
        List<String> stops = new ArrayList<>();
        for(Element row: table.getElementsByTag("tr")) {
            if (rowInd == 0) {
                for (Element span : row.getElementsByTag("span")) {
                    days.add(parseDay(span.ownText()));
                }
            } else if (rowInd == 1){
                for (Element span : row.getElementsByTag("span")) {
                    starts.add(span.ownText());
                }
            } else if (rowInd == 2){
                for (Element span : row.getElementsByTag("span")) {
                    stops.add(span.ownText());
                }
            }
            rowInd++;
        }

        for (int i=0; i<days.size(); i++) {
            map.put(days.get(i), new String[]{starts.get(i+1),stops.get(i+1)});
        }

        return map;
    }

    private DayOfWeek parseDay(String s) {
        switch (s) {
            case "Период дня":
                return null; //первая колонка
            case "Будние дни кроме пятницы":
                return DayOfWeek.MON_THU;
            case "Пятница":
                return DayOfWeek.FRIDAY;
            case "Суббота":
                return DayOfWeek.SATURDAY;
            case "Воскресенье":
                return DayOfWeek.SUNDAY;
            case "Ежедневно":
                return DayOfWeek.ALL;
            case "Будние дни":
                return DayOfWeek.MON_FRI;
            case "Выходные дни":
                return DayOfWeek.SAT_SUN;
            case "Будние и субботние дни":
                return DayOfWeek.MON_SAT;
            default:
                throw new RuntimeException("unknown day " + s);
        }
    }

    private void importRouteSchedule(RouteStop routeStop, Elements rows, Map<DayOfWeek, String[]> dayStartStop) {
        List<DayOfWeek> days = new ArrayList<>();
        boolean first = true;
        for (Element row : rows) {
            if (first) {
                for (Element span : row.getElementsByTag("span")) {
                    days.add(parseDay(span.ownText()));
                }
                first = false;
            } else {
                int i = 0;
                String from = null;
                String till = null;
                for (Element span : row.getElementsByTag("span")) {
                    if (i == 0) {
                        String period = span.ownText();
                        from = period.trim().substring(1,5);
                        till = period.trim().substring(8,13);
                    } else {
                        Integer interval = Integer.valueOf(span.ownText());
                        RouteSchedule sch = new RouteSchedule(routeStop.getRoute(), days.get(i), from, till, interval, dayStartStop.get(days.get(i))[0], dayStartStop.get(days.get(i))[1] );
                        em.get().persist(sch);
                    }
                    i++;
                }

            }
        }

    }
}
