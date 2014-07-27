package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class SearchActivity extends Activity {
    private RouteNumKeyboard routeNumKeyboard;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        final EditText txtRouteNum = (EditText) findViewById(R.id.txtRouteNum);
        routeNumKeyboard = new RouteNumKeyboard(this, R.id.routeNumKbd, R.xml.route_num_keyboard, new Runnable() {
            @Override
            public void run() {
                final Intent intent = new Intent(SearchActivity.this, RouteActivity.class);
                intent.putExtra(RouteActivity.ROUTE_NUM, txtRouteNum.getText().toString());
                startActivity(intent);
            }
        });

        routeNumKeyboard.registerEditText(R.id.txtRouteNum);
    }

    @Override
    public void onBackPressed() {
        if (routeNumKeyboard.isCustomKeyboardVisible()) {
            routeNumKeyboard.hideCustomKeyboard();
        } else {
            this.finish();
        }
    }
}
