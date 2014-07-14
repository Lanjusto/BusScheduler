package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public abstract class Menu extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupMenu() {
        final View menu = findViewById(R.id.menu);
        // Кнопка поиска
        final ImageButton btnSearch = (ImageButton) menu.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Menu.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }
}
