package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
//import ru.lanjusto.busscheduler.client.Client;

public class RouteActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);

        ((TextView) findViewById(R.id.routeNum)).setText("Троллейбус 78");

        //new Client().getRoutes();
    }
}