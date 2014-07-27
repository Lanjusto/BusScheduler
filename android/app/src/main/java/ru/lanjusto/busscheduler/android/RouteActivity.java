package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import ru.lanjusto.busscheduler.client.Client;
import ru.lanjusto.busscheduler.client.DataIsNotAvailableException;
import ru.lanjusto.busscheduler.common.model.Route;
import ru.lanjusto.busscheduler.common.model.RouteStop;

import java.util.List;

public class RouteActivity extends Activity {
    public final static String ROUTE_NUM = "RouteNum";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);

        final Intent intent = getIntent();
        final String routeNum = intent.getStringExtra(ROUTE_NUM);

        final Client client = new Client();
        final Route route = client.getRouteByNum(routeNum != null ? routeNum : "78");


        ((TextView) findViewById(R.id.routeNum)).setText(route.getImage());
        ((TextView) findViewById(R.id.routeCaption)).setText(route.getDescription());

        try {
            final List<RouteStop> routeStops = new Client().getRouteStops(73L);

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