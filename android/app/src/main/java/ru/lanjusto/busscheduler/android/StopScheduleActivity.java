package ru.lanjusto.busscheduler.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import ru.lanjusto.busscheduler.client.Client;
import ru.lanjusto.busscheduler.common.model.Time;

import java.util.List;

public class StopScheduleActivity extends Menu {
    private final long startTime = System.currentTimeMillis();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_schedule);

        // Меню
        setupMenu();

        // находим список
        final ListView lstStopSchedule = (ListView) findViewById(R.id.lstStopSchedule);

        final List<StopScheduleItem> stopScheduleItems = new Client().getStopScheduleItems();

        // создаем адаптер
        final StopScheduleListAdapter adapter = new StopScheduleListAdapter(StopScheduleActivity.this, R.layout.stop_schedule_item, stopScheduleItems);

        // присваиваем адаптер списку
        lstStopSchedule.setAdapter(adapter);

        // обновление оставшегося до отправления времени
        final Handler timerHandler = new Handler();
        final Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                adapter.updateRestingTime(Time.now());
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);

        // ссылка на дисклеймер
        final TextView disclaimerLink = (TextView) findViewById(R.id.disclaimerLink);
        disclaimerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StopScheduleActivity.this, DisclaimerActivity.class);
                startActivity(intent);
            }
        });
    }
}
