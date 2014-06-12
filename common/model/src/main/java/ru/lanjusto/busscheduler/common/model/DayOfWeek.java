package ru.lanjusto.busscheduler.common.model;

/**
 * Для сжатия данных. Кто-то указывает по каждому дню расписание, ктото пн-сб, ктото всю неделю, чаще рабочие-выходные..
 * todo Женя может всегда по дням недели хранить???
 *
 * UPD: Для клиентской части имеет смысл сохранять разбивку расписаний по дням (типа, это расписание действует по
 * будням, а это по выходным или это с понедельника по четверг, а это с пятницы по воскресенье). Хранить по всем дням
 * недели неразумно (и клиентская часть не поймёт, в какие дни расписание общее, и очень большое дублирование
 * информации). Пусть будет так (вместо enum'а можно было бы хранить и маску (типа 1111100), но видимо возможных
 * вариантов не так много, так что пусть будет enum.
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
    MON_THU,
    FRI_SUN,
    MON_SAT


}
