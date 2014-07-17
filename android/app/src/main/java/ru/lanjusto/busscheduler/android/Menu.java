package ru.lanjusto.busscheduler.android;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

public abstract class Menu {
    private Menu() {

    }

    static void setupMenu(View menu, final Context context) {
        // Кнопка поиска
        final ImageButton btnSearch = (ImageButton) menu.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(context, SearchActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
