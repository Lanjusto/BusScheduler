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
    private final City city = City.PETERBURG;

    public MosTransAvto(Provider<EntityManager> em, RouteMergeService routeMergeService) {
        this.em = em;
        this.routeMergeService = routeMergeService;
    }

    @Override
    public void pickData(Date expireDate) throws IOException, ParseException {
        em.get().getTransaction().begin();

        List<String> routes = getRouteRefs();

        for (String routeRef : routes) {
            List<RouteStop> routeStops = importRouteStops(routeRef, expireDate);

            for (RouteStop routeStop : routeStops) {
                importRouteStopSchedule(routeStop);
            }

            em.get().getTransaction().commit();
            em.get().clear(); //иначе контекст вырастает до огромных размеров
            em.get().getTransaction().begin();
        }
    }

    private void importRouteStopSchedule(RouteStop routeStop) throws IOException, ParseException {
        final URL urlUrl = new URL("http://www.mostransavto.ru/services/passenger_traffic/schedule_by_bus.php" +
                "?route_id="+routeStop.getRoute().getSourceId()
                +"&direction="+ (routeStop.getDirection().equals(Direction.AB) ? 0 : 1)
                +"&stop=-"+routeStop.getStop().getSourceId().substring(6));
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();
        urlConnection.setRequestMethod("POST");

        final String answer = Browser.readAnswer(urlConnection, Charset.forName("UTF-8"));
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(answer);
        JSONArray jsonRoutes = (JSONArray) ((JSONObject)json.get("schedule")).get("routes");

        Assert.isTrue(jsonRoutes.size() == 1); // подозрительный массив

        JSONObject schedule = (JSONObject) ((JSONObject) jsonRoutes.get(0)).get("schedule");
        for (String day : (Set<String>)schedule.keySet()) {
            DayOfWeek dayOfWeek = parseDay(day);
            RouteStopSchedule routeStopSchedule = new RouteStopSchedule(routeStop, dayOfWeek);
            em.get().persist(routeStopSchedule);

            JSONObject jsonDay = (JSONObject) schedule.get(day);
            for (String hour : (Set<String>) jsonDay.keySet()) {
                Collection<String> minutes = (JSONArray) jsonDay.get(hour);
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

    private List<RouteStop> importRouteStops(String routeRef, Date expireDate) throws IOException, ParseException {
        List<RouteStop> routeStops = new ArrayList<>();

        final URL urlUrl = new URL("http://www.mostransavto.ru/services/passenger_traffic/find_route_by_number.php?stopOrRouteId="+routeRef);
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();

        urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest"); // без этого пустой ответ
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
        Route route = routeMergeService.getRouteById(city, source, routeId, type, num, name);

        if (route.getUpdateTime() != null && expireDate.compareTo(route.getUpdateTime()) < 0) {
            return null;
        }
        log.info(route.toString());

        routeMergeService.clearRoute(route);

        JSONArray threads = (JSONArray) info.get("threads");
        Assert.isTrue(threads.size() == 1); //непонятный массив

        JSONArray jsonStops = (JSONArray) ((JSONObject)threads.get(0)).get("stops");
        Iterator<JSONObject> routesIterator = jsonStops.iterator();
        int i = 0;
        while (routesIterator.hasNext()) {
            JSONObject jsonStop = routesIterator.next();
            String stopName = (String) jsonStop.get("name");
            String stopId = (String) jsonStop.get("id");
            Double lat = (Double) jsonStop.get("latitude");
            Double lon = (Double) jsonStop.get("longitude");

            Stop stop = routeMergeService.mergeStopById(stopName, city, source, new Coordinates(lat, lon), stopId);

            routeStops.add(RouteStop.create(route, stop, Direction.AB, i));
        }

        route.setUpdateTime(new Date());
        return routeStops;
    }

    private VehicleType parseType(String type) {
        switch (type) {
            case "minibus": return VehicleType.MINIBUS;
            default: throw new RuntimeException("Неизвестный вид транспорта "+type);
        }
    }

    private List<String> getRouteRefs() throws IOException {
        List<String> routes = new ArrayList<>();

        List<String> regions = getRegions();
        for (String region : regions) {
            routes.addAll(getRegionRoutes(region));
        }

        return routes;
    }

    private List<String> getRegions() throws IOException {
        List<String> regions = new ArrayList<>();

        final URL urlUrl = new URL("http://www.mostransavto.ru/services/passenger_traffic/routes.php");
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();

        urlConnection.setRequestProperty("Cookie", "location_region=istrinskiy");
        final String answer = Browser.readAnswer(urlConnection, Charset.forName("UTF-8"));

        Document document = Jsoup.parseBodyFragment(answer);
        Elements elements = document.getElementsByClass("b-region_select").get(0).getElementsByTag("a");
        for (Element element : elements) {
            regions.add(element.attr("data-region-code"));
        }

        return regions;
    }

    private List<String> getRegionRoutes(String region) throws IOException {
        List<String> routes = new ArrayList<>();

        final URL urlUrl = new URL("http://www.mostransavto.ru/services/passenger_traffic/routes.php");
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();

        urlConnection.setRequestProperty("Cookie", "location_region="+region);
        final String answer = Browser.readAnswer(urlConnection, Charset.forName("UTF-8"));

        Document document = Jsoup.parseBodyFragment(answer);
        Elements elements = document.getElementsByClass("js-route");
        for (Element element : elements) {
            routes.add(element.attr("value"));
        }

        return routes;
    }
}
