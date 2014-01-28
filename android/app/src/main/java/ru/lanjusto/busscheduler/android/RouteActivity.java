package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import ru.lanjusto.busscheduler.client.Client;
import ru.lanjusto.busscheduler.client.DataIsNotAvailableException;
import ru.lanjusto.busscheduler.common.model.RouteStop;

import java.util.ArrayList;
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

        final Button btnCancel = (Button) findViewById(R.id.btn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final List<RouteStop> routeStops = new Client().getRouteStops(732L);
                    final List<String> names = new ArrayList<String>();
                    for (RouteStop routeStop : routeStops) {
                        names.add(routeStop.toString());
                    }

                    // находим список
                    final ListView lvMain = (ListView) findViewById(R.id.routeStopList);

                    // создаем адаптер
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(RouteActivity.this, android.R.layout.simple_list_item_1, names);

                    // присваиваем адаптер списку
                    lvMain.setAdapter(adapter);
                } catch (DataIsNotAvailableException e) {
                    Log.wtf("Lanjusto", e.getMessage());
                }
            }
        });
    }
}