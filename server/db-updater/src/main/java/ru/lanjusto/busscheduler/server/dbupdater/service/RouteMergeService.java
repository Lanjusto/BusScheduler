package ru.lanjusto.busscheduler.server.dbupdater.service;

import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lanjusto.busscheduler.common.model.City;
import ru.lanjusto.busscheduler.common.model.Coordinates;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.model.RouteStopSchedule;
import ru.lanjusto.busscheduler.common.model.Stop;
import ru.lanjusto.busscheduler.common.model.VehicleType;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * Created by Анечка on 28.05.2014.
 */
public class RouteMergeService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Provider<EntityManager> em;

    public RouteMergeService(Provider<EntityManager> em) {
        this.em = em;
    }

    /**
     * Найти или создать маршрут
     *
     * @param city
     * @param num
     * @param type
     * @param source
     * @param forceSource
     * @return
     */
    public Route getRoute(City city, String num, VehicleType type, String source, String description, boolean forceSource) {
        List<Route> routes = em.get().createQuery("select r from Route r where r.city=:city and r.num=:num and r.vehicleType=:type", Route.class)
                .setParameter("city", city)
                .setParameter("num", num)
                .setParameter("type", type)
                .getResultList();

        if (routes.size() > 1) {
            throw new RuntimeException("Найдено более одного маршрута");
        }

        if (routes.size() == 1) {
            if (!routes.get(0).getSource().equals(source) && !forceSource) {
                throw new RuntimeException("Маршрут импортирован из другого источника");
            }
            return routes.get(0);
        }

        Route route = new Route(type, num, description, city, source, null);
        em.get().persist(route);

        return route;
    }

    public Route getRouteById(City city, String source, String sourceId, VehicleType type, String num, String description) {
        List<Route> routes = em.get().createQuery("select r from Route r where r.city=:city and r.source=:source and r.sourceId=:sourceId", Route.class)
                .setParameter("city", city)
                .setParameter("source", source)
                .setParameter("sourceId", sourceId)
                .getResultList();

        if (routes.size() > 1) {
            throw new RuntimeException("Найдено более одного маршрута");
        }

        if (routes.size() == 1) {
            return routes.get(0);
        }

        Route route = new Route(type, num, description, city, source, sourceId);
        em.get().persist(route);

        return route;
    }

    public Stop mergeStopById(String name, City city, String source, Coordinates coordinates, String sourceId) {
        Stop stop;
        List<Stop> stops = em.get()
                .createQuery("select s from Stop s where s.city=:city and s.source=:source and s.sourceId=:sourceId", Stop.class)
                .setParameter("city", city)
                .setParameter("source", source)
                .setParameter("sourceId", sourceId)
                .getResultList();
        if (stops.size() == 1) {
            stop = stops.get(0);
            if (!stop.getName().equals(name)) {
                stop.setName(name);
            }
            if (stop.getCoordinates() != null && !stop.getCoordinates().equals(coordinates)) {
                stop.setCoordinates(coordinates);
            }
            return stop;
        } else if (stops.size() > 1) {
            throw new RuntimeException("обнаружено 2 остановки из одного источника с одним id");
        } else {
            stop = new Stop(name, coordinates, city, new Date(), source, sourceId);
            em.get().persist(stop);
        }

        stop.setUpdateTime(new Date());
        return stop;
    }

    public Stop mergeStop(String name, City city, String source, Coordinates coordinates) {
        // в том же источнике по имени и координатам
        // потом вообще везде по сусекам

        List<Stop> stops = em.get()
                .createQuery("select s from Stop s where s.name=:name and s.city=:city and s.source=:source", Stop.class)
                .setParameter("name", name)
                .setParameter("city", city)
                .setParameter("source", source)
                .getResultList();

        if (stops.size() > 0) {
            //ищем  с теми же координатами (если они есть...)
            for (Stop stop : stops) {
                if ((stop.getCoordinates() == null && coordinates == null)
                    || (stop.getCoordinates() != null && stop.getCoordinates().equals(coordinates))) {
                    stop.setUpdateTime(new Date());
                    return stop;
                }
            }
        } else {
            // поищем в других источниках...
            stops = em.get()
                    .createQuery("select s from Stop s where s.name=:name and s.city=:city", Stop.class)
                    .setParameter("name", name)
                    .setParameter("city", city)
                    .getResultList();

            if (stops.size() > 0) {
                //ищем  с теми же координатами (если они есть...)
                for (Stop stop : stops) {
                    if (coordinates == null || stop.getCoordinates().equals(coordinates)) {
                        stop.setUpdateTime(new Date());
                        return stop;
                    }
                }
            }
        }

        // не нашли, создадим новую
        Stop stop = new Stop(name, coordinates, city, new Date(), source);
        em.get().persist(stop);
        return stop;
    }

    public void clearRoute(Route route) {
        // удаляем остановки
        List<RouteStop> routeStops = em.get().createQuery("select rs from RouteStop rs where rs.route.id = :id", RouteStop.class)
                .setParameter("id", route.getId())
                .getResultList();
        for (RouteStop routeStop : routeStops) {
            // удаляем расписания остановки
            List<RouteStopSchedule> stopSchedules = em.get().createQuery("select rs from RouteStopSchedule rs where rs.routeStop = :rs", RouteStopSchedule.class)
                    .setParameter("rs", routeStop)
                    .getResultList();
            for (RouteStopSchedule stopSchedule : stopSchedules) {
                stopSchedule.getTimes().clear();
                em.get().remove(stopSchedule);
            }

            em.get().remove(routeStop);
        }

        // удаляем расписания
        em.get().createQuery("delete from RouteSchedule rs where rs.route = :route")
                .setParameter("route", route)
                .executeUpdate();
    }

    public Stop findStop(City city, String source, String sourceId) {
        try {
            return em.get().createQuery("select s from Stop s where s.city=:city and source=:source and sourceId=:sourceId", Stop.class)
                    .setParameter("city", city)
                    .setParameter("source", source)
                    .setParameter("sourceId", sourceId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Не найдена остановка id "+sourceId);
        }
    }
}
