package ru.lanjusto.busscheduler.server.api.timetable.timetablegetter.mgt.org;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.lanjusto.busscheduler.common.model.Time;
import ru.lanjusto.busscheduler.common.model.Timetable;
import ru.lanjusto.busscheduler.common.utils.Assert;
import ru.lanjusto.busscheduler.server.api.browser.HtmlParser;
import ru.lanjusto.busscheduler.server.api.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Парсер расписаний с www.mosgortrans.org
 */
class MgtOrgParser {
    private final String attributeName = "class";
    private final String hoursValue = "hour";
    private final String minutesValue = "minutes";

    @NotNull
    public Timetable extractTimetable(@NotNull String html) {
        final Elements elements = getElements(html);
        final List<Time> timeList = new ArrayList<Time>();

        int hours = -1;
        int minutes;
        for (Element element : elements) {
            final String attributeValue = element.attr(attributeName);
            final String content = element.text();
            if (content.isEmpty()) {
                continue;
            } else if (attributeValue.equals(hoursValue)) {
                hours = Converter.toInteger(content);
            } else if (attributeValue.equals(minutesValue)) {
                minutes = Converter.toInteger(content);

                timeList.add(new Time(hours, minutes));
            } else {
                Assert.fail();
            }
        }
        return new Timetable(timeList);
    }

    @NotNull
    private Elements getElements(@NotNull String html) {
        final Elements spanElements = HtmlParser.getElements(html, "span");
        final Elements resultElements = new Elements();
        for (Element e : spanElements) {
            if (e.attr(attributeName).equals(hoursValue) || e.attr(attributeName).equals(minutesValue)) {
                resultElements.add(e);
            }
        }
        return resultElements;
    }
}