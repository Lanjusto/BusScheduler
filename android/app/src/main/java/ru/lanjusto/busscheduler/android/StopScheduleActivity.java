package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import ru.lanjusto.busscheduler.common.model.Time;

import java.util.ArrayList;
import java.util.List;

public class StopScheduleActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_schedule);

        // находим список
        final ListView lstStopSchedule = (ListView) findViewById(R.id.lstStopSchedule);

        final List<StopScheduleItem> stopScheduleItems = new ArrayList<StopScheduleItem>();
        stopScheduleItems.add(new StopScheduleItem(new Time(1, 52), "через 19 минут", "A Н1", "Аэропорт «Шереметьево»—Терминал B"));
        stopScheduleItems.add(new StopScheduleItem(new Time(2, 22), "через 49 минут", "A Н1", "Аэропорт «Шереметьево»—Терминал B"));
        stopScheduleItems.add(new StopScheduleItem(new Time(2, 52), "через 1 час 19 минут", "A Н1", "Аэропорт «Шереметьево»—Терминал B"));
        stopScheduleItems.add(new StopScheduleItem(new Time(3, 22), "через 1 час 49 минут", "A Н1", "Аэропорт «Шереметьево»—Терминал B"));
        stopScheduleItems.add(new StopScheduleItem(new Time(3, 52), "через 2 часа 19 минут", "A Н1", "Аэропорт «Шереметьево»—Терминал B"));
        stopScheduleItems.add(new StopScheduleItem(new Time(5, 16), "через 3 часа 43 минуты", "Тб 20", "Серебряный бор"));

        // создаем адаптер
        final StopScheduleListAdapter adapter = new StopScheduleListAdapter(StopScheduleActivity.this, R.layout.stop_schedule_item, stopScheduleItems);

        // присваиваем адаптер списку
        lstStopSchedule.setAdapter(adapter);
    }
}
