package ru.lanjusto.busscheduler.server.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.server.api.timetable.GeneralTimetableGetter;
import ru.lanjusto.busscheduler.server.api.timetable.ITimetableGetter;
import ru.lanjusto.busscheduler.server.api.timetable.NoTimetableAvailableException;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class DataProvider implements IDataProvider {
    private final Cache<String, List<Route>> routesCache;
    private final Cache<Long, List<RouteStop>> routeStopsByRouteCache;
    private final Cache<Long, RouteStop> routeStopCache;

    private final Provider<EntityManager> em;
    private final ITimetableGetter timetableGetter;

    @Inject
    public DataProvider(@NotNull Provider<EntityManager> em) {
        this.em = em;
        this.timetableGetter = new GeneralTimetableGetter();
        routesCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
        routeStopsByRouteCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
        routeStopCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    @Override
    @NotNull
    public List<Route> getRoutes() {
        final String key = "routes";

        final List<Route> cachedList = routesCache.getIfPresent(key);
        if (cachedList != null) {
            return cachedList;
        }

        final List<Route> routes = em.get()
                .createQuery("SELECT r FROM Route r ORDER BY r.id", Route.class)
                .getResultList();
        routesCache.put(key, routes);
        return routes;
    }

    @Override
    @NotNull
    public List<RouteStop> getRouteStops(long routeId) {
        final List<RouteStop> cachedList = routeStopsByRouteCache.getIfPresent(routeId);
        if (cachedList != null) {
            return cachedList;
        }

        final Route route = getRouteById(routeId);
        final List<RouteStop> routeStops = em.get()
                .createQuery("SELECT rs FROM RouteStop rs WHERE rs.route = :route", RouteStop.class)
                .setParameter("route", route)
                .getResultList();
        routeStopsByRouteCache.put(routeId, routeStops);
        return routeStops;
    }

    @NotNull
    @Override
    public Timetable getTimeTable(long routeStopId) throws NoTimetableAvailableException {
        return timetableGetter.get(getRouteStopById(routeStopId));

    }

    @NotNull
    private Route getRouteById(long id) {
        final List<Route> routes = getRoutes();
        for (Route route : routes) {
            if (route.getId() == id) {
                return route;
            }
        }
        throw new IllegalArgumentException();
    }

    @NotNull
    private RouteStop getRouteStopById(long id) {
        final RouteStop cachedRouteStop = routeStopCache.getIfPresent(id);
        if (cachedRouteStop != null) {
            return cachedRouteStop;
        }

        final RouteStop routeStop = em.get()
                .createQuery("SELECT rs FROM RouteStop rs WHERE rs.id = :id", RouteStop.class)
                .setParameter("id", id)
                .getSingleResult();
        routeStopCache.put(id, routeStop);
        return routeStop;
    }

    @NotNull
    private List<RouteStop> getRouteStops() {
        final List<RouteStop> routeStops = em.get()
                .createQuery("SELECT rs FROM RouteStop rse WHERE ", RouteStop.class)
                .getResultList();

        for (RouteStop routeStop : routeStops) {
            routeStopCache.put(routeStop.getId(), routeStop);
        }
        return routeStops;
    }
}
