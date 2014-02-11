package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import ru.lanjusto.busscheduler.client.Client;
import ru.lanjusto.busscheduler.client.DataIsNotAvailableException;
import ru.lanjusto.busscheduler.common.model.RouteStop;

import java.util.List;

public class RouteActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);

        ((TextView) findViewById(R.id.routeNum)).setText("Троллейбус 78");

        try {
            final List<RouteStop> routeStops = new Client().getRouteStops(732L);

            // находим список
            final ListView lvMain = (ListView) findViewById(R.id.routeStopList);

            // создаем адаптер
            final RouteStopListAdapter adapter = new RouteStopListAdapter(RouteActivity.this, R.layout.route_stop_list_item, routeStops);

            // присваиваем адаптер списку
            lvMain.setAdapter(adapter);
        } catch (DataIsNotAvailableException e) {
            //TODO error catching
            Log.wtf("Lanjusto", e.getMessage());
        }
    }
}