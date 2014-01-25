package ru.lanjusto.busscheduler.server.dbupdater.browser;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Парсим HTML.
 */
public class HtmlParser {
    @NotNull
    public static String deleteTags(@NotNull String s) {
        final Document doc = Jsoup.parse(s);
        return doc.text();
    }

    @NotNull
    public static Elements getElements(@NotNull String html, @NotNull String tagName) {
        final Document doc = Jsoup.parse(html);
        return doc.getElementsByTag(tagName);
    }
}
