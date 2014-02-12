package ru.lanjusto.busscheduler.server.dbupdater.datapicker;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanjusto.busscheduler.common.model.Coordinates;
import ru.lanjusto.busscheduler.common.model.Direction;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.Stop;
import ru.lanjusto.busscheduler.common.model.UrlCoordinates;
import ru.lanjusto.busscheduler.common.model.VehicleType;
import ru.lanjusto.busscheduler.common.utils.Assert;
import ru.lanjusto.busscheduler.server.dbupdater.browser.Browser;
import ru.lanjusto.busscheduler.server.dbupdater.browser.HtmlParser;
import ru.lanjusto.busscheduler.server.dbupdater.typography.Typographer;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Наполнитель базы данных.
 */
@Singleton
class DataPicker implements IDataPicker {
    private final Logger log = LoggerFactory.getLogger(DataPicker.class);
    private final Map<String, Stop> stopMap = new HashMap<String, Stop>();
    private final Provider<EntityManager> em;

    @Inject
    public DataPicker(Provider<EntityManager> em) {
        this.em = em;
    }

    public void pickData() {
        em.get().getTransaction().begin();

        try {
            setupSession();
            clearDataBase();
            pickData("http://bus.ruz.net/routes/", VehicleType.BUS);
            pickData("http://trolley.ruz.net/routes/", VehicleType.TROLLEYBUS);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            em.get().getTransaction().commit();
        }
    }

    private void setupSession() {
        final String schemaName = (String) em.get().getEntityManagerFactory().getProperties().get("hibernate.default_schema");
        em.get().createNativeQuery("SET search_path TO " + schemaName).executeUpdate();
    }

    private void clearDataBase() {
        truncateTable("t_route_stop");
        truncateTable("t_stop");
        truncateTable("t_route");
        flushSequence("seq_route_stop");
        flushSequence("seq_stop");
        flushSequence("seq_route");
    }

    private void truncateTable(String tableName) {
        em.get().createNativeQuery("TRUNCATE TABLE " + tableName + " CASCADE").executeUpdate();
    }

    private void flushSequence(String sequenceName) {
        em.get().createNativeQuery("SELECT setVal('" + sequenceName + "', 1, false)").getSingleResult();
    }

    private void pickData(String initialUrl, VehicleType vehicleType) throws IOException {
        final Map<Route, String> routeMap = pickRoutes(initialUrl, vehicleType);

        /*for (String s : routeMap.values()) {
            pickRoute(s);
        }*/

        final List<Route> routeList = new ArrayList<Route>(routeMap.keySet());
        Collections.sort(routeList, new Comparator<Route>() {
            public int compare(Route o1, Route o2) {
                final String num1 = o1.getNum();
                final String num2 = o2.getNum();

                final Integer n1 = parse(num1);
                final Integer n2 = parse(num2);

                if (n1 != null && n2 != null && n1.compareTo(n2) != 0) {
                    return n1.compareTo(n2);
                } else {
                    return num1.compareTo(num2);
                }
            }

            private Integer parse(String s) {
                while (true) {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        if (s.length() == 0) {
                            return null;
                        } else {
                            s = s.substring(0, s.length() - 1);
                        }
                    }
                }

            }
        });
        final List<Route> invalidRoutes = new ArrayList<Route>();
        for (Route r : routeList) {
            try {
                pickRoute(r, routeMap.get(r));
            } catch (InvalidRouteException e) {
                invalidRoutes.add(r);
            }
        }
        System.err.println("Invalid routes:");
        for (Route r : invalidRoutes) {
            System.err.println("   " + r.toString());
        }
    }

    @NotNull
    private Map<Route, String> pickRoutes(@NotNull String initialUrl, @NotNull VehicleType vehicleType) throws IOException {
        log.info("Exploring page {}...", initialUrl);
        final String content = Browser.getContent(initialUrl);
        final String root = getRoot(initialUrl);

        final Elements elements = HtmlParser.getElements(content, "a");
        final Map<Route, String> routeMap = new HashMap<Route, String>();
        final Pattern pattern = Pattern.compile("^/routes/(\\d+)$");
        for (Element element : elements) {
            final Matcher matcher = pattern.matcher(element.attr("href"));

            if (matcher.matches()) {
                final String routeNum = element.text();
                final String relativeLink = matcher.group();

                final String absoluteLink = root + relativeLink;

                final Route route = new Route(vehicleType, routeNum);
                if (!routeMap.containsKey(route)) {
                    routeMap.put(route, absoluteLink);
                }
            }
        }
        return routeMap;
    }

    private void pickRoute(@NotNull Route route, @NotNull String url) throws IOException, InvalidRouteException {
        //log.info("Picking route {}...", route);
        final String s = Browser.getContent(url);

        // Конечные
        {
            final Elements elements = HtmlParser.getElements(s, "span");
            for (Element e : elements) {
                if (e.attr("class").equals("caption") && e.children().isEmpty()) {
                    final List<String> parts = Lists.newArrayList(Splitter.on(" - ").split(e.text()));
                    if (parts.size() != 2) {
                        throw new InvalidRouteException();
                    }
                    final String stopA = parts.get(0);
                    final String stopB = parts.get(1);
                    System.out.println(route + " (" + stopA + " → " + stopB + ")");
                    route.setDescription(Typographer.process(stopA) + " — " + Typographer.process(stopB));
                }
            }
        }


        // Путь следования
        {
            final Elements elements = HtmlParser.getElements(s, "td");
            for (Element e : elements) {
                if (e.attr("class").equals("tabcolumnextra1-notb")) {
                    final String content = e.html().replace("&quot;", "\"");

                    final List<String> streets = Lists.newArrayList(Splitter.on(CharMatcher.anyOf(";,")).trimResults().split(content));

                    /*Direction direction = Direction.BOTH;
                    for (String street : streets) {
                        if (street.contains("left.gif")) {
                            direction = Direction.AB;
                        } else if (street.contains("right.gif")) {
                            direction = Direction.BA;
                        } else if (street.contains("both.gif")) {
                            direction = Direction.BOTH;
                        } else if (street.contains("zaezd-ab.gif")) {
                            direction = Direction.VISIT_AB;
                        } else if (street.contains("zaezd-ba.gif")) {
                            direction = Direction.VISIT_BA;
                        } else if (street.contains("zaezd.gif")) {
                            direction = Direction.VISIT_BOTH;
                        }

                        final String netStreet = HtmlParser.deleteTags(street);

                        System.out.println(netStreet + " (" + direction + ")");
                    }*/
                }
            }
        }

        // Остановки
        {
            final Elements elements = HtmlParser.getElements(s, "td");
            int order = 0;

            final List<RouteStop> abStops = new ArrayList<RouteStop>();
            final List<RouteStop> baStops = new ArrayList<RouteStop>();
            final List<RouteStop> lastStops = new ArrayList<RouteStop>();
            for (Element e : elements) {
                final String classValue = e.attr("class");
                boolean stopIsEnabledAB = true;
                boolean stopIsEnabledBA = true;
                if (classValue.equals("tabcolumnstopes")) {
                    final String content = e.html().replace("&quot;", "\"");

                    final String netStop = HtmlParser.deleteTags(content);
                    final boolean isTerminal = (content.startsWith("<b>") && content.endsWith("</b>"));
                    final boolean isOnRequest = netStop.endsWith(" (по требованию)");
                    for (RouteStop rs : lastStops) {
                        rs.setOnRequest(isOnRequest);
                        rs.setTerminal(isTerminal);
                    }
                    System.out.println("   " + netStop);

                    if (order != 2) {
                        throw new IllegalArgumentException();
                    }
                    order = 0;
                    lastStops.clear();
                } else if (classValue.equals("tabcolumncolorcell")) {
                    final String text = e.text();

                    switch (order) {
                        case 0:
                            // остановка A —→ B
                            stopIsEnabledAB = text.equals("↓") || text.equals("•");
                            break;
                        case 1:
                            // остановка B —→ A
                            stopIsEnabledBA = text.equals("↑") || text.equals("•");
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                    order++;


                    final Elements aElements = HtmlParser.getElements(e.html(), "a");
                    if (!Strings.isNullOrEmpty(e.html())) {
                        switch (aElements.size()) {
                            case 0:
                                throw new InvalidRouteException();
                            case 1:
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }

                        final String stopUrl = aElements.get(0).attr("href");

                        if (!stopMap.containsKey(stopUrl)) {
                            final String fullStopUrl = getRoot(url) + stopUrl;
                            stopMap.put(stopUrl, pickStop(fullStopUrl));
                        }
                        final Stop stop = stopMap.get(stopUrl);
                        if (stopIsEnabledAB && order == 1) {
                            final RouteStop rs = RouteStop.create(route, stop, Direction.AB);
                            abStops.add(rs);
                            lastStops.add(rs);
                        }
                        if (stopIsEnabledBA && order == 2) {
                            final RouteStop rs = RouteStop.create(route, stop, Direction.BA);
                            baStops.add(rs);
                            lastStops.add(rs);
                        }
                    }
                }

            }

            for (int i = 0; i < abStops.size(); i++) {
                abStops.get(i).setOrder(i);
            }
            Collections.reverse(baStops);
            for (int i = 0; i < baStops.size(); i++) {
                baStops.get(i).setOrder(i);
            }

            em.get().persist(route);

            for (RouteStop rs : abStops) {
                em.get().persist(rs);
            }

            for (RouteStop rs : baStops) {
                em.get().persist(rs);
            }
        }
    }

    private Stop pickStop(@NotNull String url) throws IOException, InvalidRouteException {
        final String content = Browser.getContent(url);

        // Название
        String name = "";
        final Elements spanElements = HtmlParser.getElements(content, "span");
        for (Element e : spanElements) {
            if (e.attr("class").equals("caption")) {
                name = e.text();
            }
        }

        // Координаты
        Coordinates coordinates = null;
        final Elements tdElements = HtmlParser.getElements(content, "td");
        boolean isOk = false;
        for (Element e : tdElements) {
            if (e.attr("class").equals("tabcolumnextra1-notb")) {
                final Elements aElements = e.getElementsByTag("a");
                for (Element ae : aElements) {
                    if (ae.text().contains("показать на карте")) {
                        isOk = true;
                        coordinates = getCoordinateByYandexMapsUrl(ae.attr("href"));
                    }
                }
            }
        }

        if (!isOk) {
            throw new InvalidRouteException();
        }

        final Stop stop = new Stop(Typographer.process(name), coordinates);
        em.get().persist(stop);
        return stop;
    }

    private Coordinates getCoordinateByYandexMapsUrl(@NotNull String url) throws IOException {
        try {
            final UrlCoordinates urlCoordinates = em.get()
                    .createQuery("SELECT uc FROM UrlCoordinates uc WHERE uc.url = :url", UrlCoordinates.class)
                    .setParameter("url", url)
                    .getSingleResult();
            return urlCoordinates.getCoordinates();
        } catch (NoResultException e) {
            // Достаём из Яндекс.Карт
            final String correctedUrl;
            if (url.startsWith("http:/") && !url.startsWith("http://")) {
                correctedUrl = url.replace("http:/", "http://");
            } else {
                correctedUrl = url;
            }
            final URL redirected = Browser.getUrlAfterRedirecting(correctedUrl);
            final String query = redirected.getQuery();
            for (String s : Splitter.on('&').split(query)) {
                final List<String> list1 = Lists.newArrayList(Splitter.on('=').split(s));

                final String key = list1.get(0);
                final String value = list1.get(1);

                if (key.equals("ll")) {
                    final List<String> list2 = Lists.newArrayList(Splitter.on("%2C").split(value));
                    final Coordinates coordinates = new Coordinates(new BigDecimal(list2.get(1)), new BigDecimal(list2.get(0)));
                    em.get().persist(new UrlCoordinates(url, coordinates));
                    return coordinates;
                }
            }
            System.err.println(query);
            throw new IllegalArgumentException();
        }
    }


    @NotNull
    private String getRoot(@NotNull String url) {
        final Pattern pattern = Pattern.compile("^(http://[^/]+).*$");
        final Matcher matcher = pattern.matcher(url);

        Assert.isTrue(matcher.matches());

        return matcher.group(1);
    }
}
