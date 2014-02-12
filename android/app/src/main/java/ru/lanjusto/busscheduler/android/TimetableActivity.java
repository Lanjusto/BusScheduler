package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import ru.lanjusto.busscheduler.common.model.Time;

import java.util.ArrayList;
import java.util.List;

public class TimetableActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable);

        // находим список
        final ListView lstTimetable = (ListView) findViewById(R.id.lstTimetable);

        final List<Time> times = new ArrayList<Time>();
        times.add(new Time(20, 13));
        times.add(new Time(20, 37));
        times.add(new Time(21, 0));

        // создаем адаптер
        final TimetableAdapter adapter = new TimetableAdapter(TimetableActivity.this, R.layout.timetable_list_item, times);

        // присваиваем адаптер списку
        lstTimetable.setAdapter(adapter);
    }
}
