package ru.lanjusto.busscheduler.server.dbupdater.service;

import com.google.inject.Provider;
import ru.lanjusto.busscheduler.common.model.*;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * Created by Анечка on 28.05.2014.
 */
public class RouteMergeService {
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
        List<Route> routes = em.get().createQuery("select r from Route r where r.city=:city and r.num=:num and r.vehicleType=:type")
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

        Route route = new Route(type, num, description, city, source);
        em.get().persist(route);

        return route;
    }

    public Stop mergeStop(String name, City city, String source, Coordinates coordinates) {
        return mergeStop(name, city, source, coordinates, null);
    }


    public Stop mergeStop(String name, City city, String source, Coordinates coordinates, String sourceId) {
        List<Stop> stops = em.get()
                .createQuery("select s from Stop s where s.name=:name and s.city=:city and s.source=:source")
                .setParameter("name", name)
                .setParameter("city", city)
                .setParameter("source", source)
                .getResultList();

        if (stops.size() > 1) {
            //ищем  с теми же координатами
            for (Stop stop : stops) {
                if ((stop.getCoordinates() == null && coordinates == null)
                        || stop.getCoordinates().equals(coordinates)) {
                    stop.setUpdateTime(new Date());
                    return stop;
                }
            }

            // не нашли, создадим новую
            // если было две с одним именем и одна переместилась в пространстве, то старая остановка "зависнет"
            Stop stop = new Stop(name, coordinates, city, new Date(), source, sourceId);
            em.get().persist(stop);
            return stop;
        }
        if (stops.size() == 1) {
            stops.get(0).setUpdateTime(new Date());
            stops.get(0).setSourceId(sourceId);
            return stops.get(0);
        }

        // поищем в других источниках...
        stops = em.get()
                .createQuery("select s from Stop s where s.name=:name and s.city=:city")
                .setParameter("name", name)
                .setParameter("city", city)
                .getResultList();

        if (stops.size() == 1) {
            if (coordinates != null && stops.get(0).getCoordinates() == null) {
                stops.get(0).setCoordinates(coordinates);
                stops.get(0).setSource(source);
            }
            stops.get(0).setSourceId(sourceId);
            stops.get(0).setUpdateTime(new Date());

            return stops.get(0);
        }

        // не нашли, создадим новую
        Stop stop = new Stop(name, coordinates, city, new Date(), source, sourceId);
        em.get().persist(stop);
        return stop;
    }

    public void clearRoute(Route route) {
        // удаляем текущие остановки
        List<RouteStop> routeStops = em.get().createQuery("select rs from RouteStop rs where rs.route.id = :id", RouteStop.class)
                .setParameter("id", route.getId())
                .getResultList();
        for (RouteStop routeStop : routeStops) {
            em.get().remove(routeStop);
        }

        // удаляем расписания
        List<RouteSchedule> routeSchedules = em.get().createQuery("select rs from RouteSchedule rs where rs.route = :route", RouteSchedule.class)
                .setParameter("route", route)
                .getResultList();
        for (RouteSchedule routeSchedule : routeSchedules) {
            em.get().remove(routeSchedule);
        }

    }
}
