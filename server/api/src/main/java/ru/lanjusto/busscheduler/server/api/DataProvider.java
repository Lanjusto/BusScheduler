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

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class DataProvider implements IDataProvider {
    private final Cache<String, List<Route>> routesCache;
    private final Cache<Long, List<RouteStop>> routeStopsCache;

    private final Provider<EntityManager> em;
    private final ITimetableGetter timetableGetter;

    @Inject
    public DataProvider(@NotNull Provider<EntityManager> em) {
        this.em = em;
        this.timetableGetter = new GeneralTimetableGetter();
        routesCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
        routeStopsCache = CacheBuilder.newBuilder()
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
        final List<RouteStop> cachedList = routeStopsCache.getIfPresent(routeId);
        if (cachedList != null) {
            return cachedList;
        }

        final Route route = getRouteById(routeId);
        final List<RouteStop> routeStops = em.get()
                .createQuery("SELECT rs FROM RouteStop rs WHERE rs.route = :route", RouteStop.class)
                .setParameter("route", route)
                .getResultList();
        routeStopsCache.put(routeId, routeStops);
        return routeStops;
    }

    @NotNull
    @Override
    public Timetable getTimeTable(long routeId) {
        return timetableGetter.get(getRouteById(routeId));
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
}
