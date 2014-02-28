package ru.lanjusto.busscheduler.common.utils;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

public abstract class CommonData {
    public static String SERVER = "172.29.1.82";
    public static int PORT = 8182;
    public static String URL = SERVER + ":" + PORT;
    public static String HTTP_PREFIX = "http://";

    public static String ROUTES_PAGE = "/routes";
    public static String ROUTE_PAGE = "/routes/{routeId}";
    public static String TIMETABLE_PAGE = "/timetable/{routeStopId}/{timeTableKind}";

    public static String ROUTES_URL = HTTP_PREFIX + URL + ROUTES_PAGE;
    public static String ROUTE_URL = HTTP_PREFIX + URL + ROUTE_PAGE;
    public static String TIMETABLE_URL = HTTP_PREFIX + URL + TIMETABLE_PAGE;

    public static HierarchicalStreamDriver getXStreamDriver() {
        //return new JettisonMappedXmlDriver();
        //TODO временно перешёл на XML. Json на Android почему-то не фурычит
        return new XppDriver();
    }
}
