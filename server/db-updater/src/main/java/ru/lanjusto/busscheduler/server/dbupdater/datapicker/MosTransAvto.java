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
import ru.lanjusto.busscheduler.common.utils.Assert;
import ru.lanjusto.busscheduler.server.dbupdater.browser.Browser;
import ru.lanjusto.busscheduler.server.dbupdater.service.RouteMergeService;

import javax.persistence.EntityManager;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * МОСТРАНСАВТО
 *
 * подумал, что правильнее как имя собственное назвать ближе к русскому произношению
 */
public class MosTransAvto implements IDataPicker {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Provider<EntityManager> em;
    private final RouteMergeService routeMergeService;

    private final String source = "MosTransAvto";
    private final City city = City.MOSCOW;

    public MosTransAvto(Provider<EntityManager> em, RouteMergeService routeMergeService) {
        this.em = em;
        this.routeMergeService = routeMergeService;
    }

    @Override
    public void pickData(Date expireDate) throws IOException, ParseException {
        em.get().getTransaction().begin();

        List<Route> routes = getRoutes();

        for (Route route : routes) {
            if (route.getUpdateTime() == null || expireDate.compareTo(route.getUpdateTime()) > 0) {
                log.info(route.toString());
                route = em.get().merge(route);

                routeMergeService.clearRoute(route);
                List<RouteStop> routeStops = importRouteStops(route, Direction.AB);
                routeStops.addAll(importRouteStops(route, Direction.BA));
                for (RouteStop routeStop : routeStops) {
                    importRouteStopSchedule(routeStop);
                }

                route.setUpdateTime(new Date());

                em.get().getTransaction().commit();
                em.get().clear(); //иначе контекст вырастает до огромных размеров
                em.get().getTransaction().begin();
            }
        }
    }

    private void importRouteStopSchedule(RouteStop routeStop) throws IOException, ParseException {
        final URL urlUrl = new URL("http://www.mostransavto.ru/services/passenger_traffic/schedule_by_bus.php" +
                "?route_id="+routeStop.getRoute().getSourceId()
                +"&direction="+ (routeStop.getDirection().equals(Direction.AB) ? 0 : 1)
                +"&stop="+routeStop.getStop().getSourceId());
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        urlConnection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.writeBytes("route_id=" + routeStop.getRoute().getSourceId());
        wr.flush();
        wr.close();

        log.info(routeStop.toString());
        final String answer = Browser.readAnswer(urlConnection, Charset.forName("UTF-8"));
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(answer);
        JSONObject o = (JSONObject)json.get("schedule");
        JSONObject jsonRoutes = null;
        if (o.get("routes") instanceof JSONObject) {
            jsonRoutes = (JSONObject) o.get("routes");
        } else {
            if (((JSONArray) o.get("routes")).size() == 0) {
                return; //вообще нет расписания
            } else {
                throw new RuntimeException("непойми что");
            }

        }

        Assert.isTrue(jsonRoutes.size() == 1); // подозрительный массив

        //jsonRoutes.
        String key = (String) jsonRoutes.keySet().iterator().next();
        JSONObject schedule = (JSONObject) ((JSONObject) jsonRoutes.get(key)).get("schedule");
        for (String day : (Set<String>)schedule.keySet()) {
            DayOfWeek dayOfWeek = parseDay(day);
            RouteStopSchedule routeStopSchedule = new RouteStopSchedule(routeStop, dayOfWeek);
            em.get().persist(routeStopSchedule);

            JSONObject jsonDay = (JSONObject) schedule.get(day);
            for (String hour : (Set<String>) jsonDay.keySet()) {
                Collection<String> minutes = null;
                if (jsonDay.get(hour) instanceof JSONArray) {
                    minutes = (JSONArray) jsonDay.get(hour);
                } else {
                    minutes = ((JSONObject)jsonDay.get(hour)).values();
                }
                for (String minute : minutes) {
                    Date time = new Date((Long.valueOf(hour) * 60 + Long.valueOf(minute.trim())) * 60_000);
                    routeStopSchedule.getTimes().add(new ScheduleTime(time));
                }
            }
        }
    }

    private DayOfWeek parseDay(String day) {
        switch (day) {
            case "1": return DayOfWeek.MONDAY;
            case "2": return DayOfWeek.THURSDAY;
            case "3": return DayOfWeek.WEDNESDAY;
            case "4": return DayOfWeek.THURSDAY;
            case "5": return DayOfWeek.FRIDAY;
            case "6": return DayOfWeek.SATURDAY;
            case "7": return DayOfWeek.SUNDAY;
            default: throw new RuntimeException("неизвестный день "+day);
        }
    }

    private List<RouteStop> importRouteStops(Route route, Direction direction) throws IOException, ParseException {
        List<RouteStop> routeStops = new ArrayList<>();

        final URL urlUrl = new URL("http://www.mostransavto.ru/services/passenger_traffic/schedule_by_bus.php" +
                "?route_id="+route.getSourceId()
                +"&direction="+ (direction.equals(Direction.AB) ? 0 : 1));
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        urlConnection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.writeBytes("route_id=" + route.getSourceId());
        wr.flush();
        wr.close();

        final String answer = Browser.readAnswer(urlConnection, Charset.forName("UTF-8"));
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(answer);
        JSONArray jsonStops = (JSONArray) json.get("stops");
        Iterator<JSONObject> stopIterator = jsonStops.iterator();
        int i = 0;
        while (stopIterator.hasNext()) {
            JSONObject jsonStop = stopIterator.next();
            if (jsonStop.get("ycode") == null) {
                continue;
            }
            String name = (String) jsonStop.get("name");
            String id = (String) jsonStop.get("code");
            Double lat = Double.valueOf((String) jsonStop.get("latitude"));
            Double lon = Double.valueOf((String) jsonStop.get("longitude"));
            Stop stop = routeMergeService.mergeStopById(name, city, source, new Coordinates(lat, lon), id);
            routeStops.add(RouteStop.create(route, stop, direction, i++));
        }


/*
Доставание остановок из http://www.mostransavto.ru/services/passenger_traffic/find_route_by_number.php?stopOrRouteId="+routeRef

        JSONArray threads = (JSONArray) info.get("threads");
        Assert.isTrue(threads.size() == 2); //непонятный массив
        Iterator<JSONObject> threadIterator = threads.iterator();
        Direction direction = Direction.AB;
        while (threadIterator.hasNext()) {
            JSONObject thread = threadIterator.next();

            JSONArray jsonStops = (JSONArray) thread.get("stops");
            Iterator<JSONObject> stopsIterator = jsonStops.iterator();
            int i = 0;
            while (stopsIterator.hasNext()) {
                JSONObject jsonStop = stopsIterator.next();
                String stopName = (String) jsonStop.get("name");
                String stopId = (String) jsonStop.get("id");
                Double lat = Double.valueOf((String) jsonStop.get("latitude"));
                Double lon = Double.valueOf((String) jsonStop.get("longitude"));

                Stop stop = routeMergeService.mergeStopById(stopName, city, source, new Coordinates(lat, lon), stopId);

                routeStops.add(RouteStop.create(route, stop, direction, i));
            }

            direction = Direction.BA;
        }
*/

        route.setUpdateTime(new Date());
        return routeStops;
    }

    private VehicleType parseType(String type) {
        switch (type) {
            case "minibus": return VehicleType.MINIBUS;
            case "bus": return VehicleType.BUS;
            default: throw new RuntimeException("Неизвестный вид транспорта "+type);
        }
    }

    private List<Route> getRoutes() throws IOException, ParseException {
        List<Route> routes = new ArrayList<>();

        List<String> regions = getRegions();
        for (String region : regions) {
            log.info("список маршрутов района "+region);
            routes.addAll(getRegionRoutes(region));
        }

        return routes;
    }

    private List<String> getRegions() throws IOException {
        List<String> regions = new ArrayList<>();

        final URL urlUrl = new URL("http://www.mostransavto.ru/services/passenger_traffic/routes.php");
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();

        urlConnection.setRequestProperty("Cookie", "location_region=istrinskiy");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        final String answer = Browser.readAnswer(urlConnection, Charset.forName("UTF-8"));

        Document document = Jsoup.parseBodyFragment(answer);
        Elements elements = document.getElementsByClass("b-region_select").get(0).getElementsByTag("a");
        for (Element element : elements) {
            regions.add(element.attr("data-region-code"));
        }

        return regions;
    }

    private List<Route> getRegionRoutes(String region) throws IOException, ParseException {
        List<Route> routes = new ArrayList<>();

        final URL urlUrl = new URL("http://www.mostransavto.ru/services/passenger_traffic/routes.php");
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();

        urlConnection.setRequestProperty("Cookie", "location_region="+region);
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        final String answer = Browser.readAnswer(urlConnection, Charset.forName("UTF-8"));

        Document document = Jsoup.parseBodyFragment(answer);
        Elements elements = document.getElementsByClass("js-route");
        for (Element element : elements) {
            routes.add(getRoute(element.attr("value")));
        }

        return routes;
    }

    private Route getRoute(String routeRef) throws IOException, ParseException {
        final URL urlUrl = new URL("http://www.mostransavto.ru/services/passenger_traffic/find_route_by_number.php?stopOrRouteId="+routeRef);
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();

        urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest"); // без этого пустой ответ
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        urlConnection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.writeBytes("stopOrRouteId=" + routeRef);
        wr.flush();
        wr.close();

        final String answer = Browser.readAnswer(urlConnection, Charset.forName("UTF-8"));
        JSONParser parser = new JSONParser();
        JSONArray json = (JSONArray) parser.parse(answer);

        Assert.isTrue(json.size() == 1); //непонятный массив
        JSONObject info = (JSONObject) json.get(0);

        String name  = (String) info.get("name");
        String num = (String) info.get("number");
        VehicleType type = parseType((String) info.get("type"));
        String routeId = (String) info.get("id");
        return routeMergeService.getRouteById(city, source, routeId, type, num, name);
    }
}
