package ru.lanjusto.busscheduler.server.dbupdater.browser;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.utils.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Типа, браузер
 */
//todo вот вот - разбили проект по частям и приходится клонировать "Браузер"
public class Browser {
    @NotNull
    public static String getContent(@NotNull String url, Charset charset) throws IOException {
        final URL urlUrl = new URL(url);
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();
        return readAnswer(urlConnection, charset);
    }

    public static URL getUrlAfterRedirecting(@NotNull String url) throws IOException {
        final URL urlUrl = new URL(url);
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();
        /*switch (urlConnection.getResponseCode()) {
            case HttpURLConnection.HTTP_OK:
            case HttpURLConnection.HTTP_UNAVAILABLE:
                break;
            default:
                System.err.println(url);
                System.err.println(urlConnection.getResponseCode());
                System.err.println(urlConnection.getURL());
                Assert.fail();
        }*/
        urlConnection.getResponseCode();
        return urlConnection.getURL();
    }

    public static String readAnswer(HttpURLConnection urlConnection, Charset charset) throws IOException {
        Assert.equals(urlConnection.getResponseCode(), HttpURLConnection.HTTP_OK);

        final StringBuilder sb = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), charset))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
        }

        return sb.toString();
    }



}
