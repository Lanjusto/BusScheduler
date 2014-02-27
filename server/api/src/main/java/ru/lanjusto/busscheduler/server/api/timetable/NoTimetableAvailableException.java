package ru.lanjusto.busscheduler.server.api.timetable;

/**
 * Исключение "Недоступно расписание автобусов".
 * Mike: дописал exception, поскольку в репозитории название класса было NoTimeTableAvailable.java,
 * а имя класса NoTimetableAvailable - видимо в винде изменение имени класса не добавило файл в индекс гита,
 * поэтому так и рассинхронилось прозрачно для того, у кого файл уже был поменян локально.
 */
public class NoTimetableAvailableException extends Exception {


}
