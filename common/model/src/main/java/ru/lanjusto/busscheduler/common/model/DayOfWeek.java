package ru.lanjusto.busscheduler.common.model;

/**
 * Для сжатия данных. Кто-то указывает по каждому дню расписание, ктото пн-сб, ктото всю неделю, чаще рабочие-выходные..
 * todo Женя может всегда по дням недели хранить???
 */
public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    ALL,
    MON_FRI,
    SAT_SUN,
    MON_THU


}
