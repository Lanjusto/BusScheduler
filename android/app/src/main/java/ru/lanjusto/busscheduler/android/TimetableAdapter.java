package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.lanjusto.busscheduler.common.model.Time;

import java.util.List;

public class TimetableAdapter extends ArrayAdapter<Time> {
    private final Context context;
    private final int layoutResourceId;
    private final List<Time> data;

    TimetableAdapter(Context context, int layoutResourceId, List<Time> data) {
        super(context, layoutResourceId, data);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final TimeHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TimeHolder();
            holder.txtDepartureTime = (TextView) row.findViewById(R.id.txtDepartureTime);
            holder.txtRestingTime = (TextView) row.findViewById(R.id.txtRestingTime);

            row.setTag(holder);
        } else {
            holder = (TimeHolder) row.getTag();
        }

        final Time time = data.get(position);
        holder.txtDepartureTime.setText(time.toString());
        holder.txtRestingTime.setText("(через 15 минут)");

        return row;
    }

    static class TimeHolder {
        TextView txtDepartureTime;
        TextView txtRestingTime;
    }
}
