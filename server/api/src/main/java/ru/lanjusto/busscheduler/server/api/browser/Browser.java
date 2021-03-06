package ru.lanjusto.busscheduler.server.api.browser;

import org.jetbrains.annotations.NotNull;
import ru.lanjusto.busscheduler.common.utils.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Типа, браузер
 */
public class Browser {
    @NotNull
    public static String getContent(@NotNull String url) throws IOException {
        final URL urlUrl = new URL(url);
        final HttpURLConnection urlConnection = (HttpURLConnection) urlUrl.openConnection();
        Assert.equals(urlConnection.getResponseCode(), HttpURLConnection.HTTP_OK);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.forName("CP1251")))) {
            final StringBuilder sb = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
            return sb.toString();
        }
    }
}
