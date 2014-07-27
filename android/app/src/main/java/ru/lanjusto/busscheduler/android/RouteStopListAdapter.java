package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.lanjusto.busscheduler.common.model.RouteStop;

import java.util.List;

class RouteStopListAdapter extends ArrayAdapter<RouteStop> {
    private final Context context;
    private final int layoutResourceId;
    private final List<RouteStop> data;

    RouteStopListAdapter(Context context, int layoutResourceId, List<RouteStop> data) {
        super(context, layoutResourceId, data);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final RouteStopHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RouteStopHolder();
            holder.nameView = (TextView) row.findViewById(R.id.routeStopName);
            holder.txtMemo = (TextView) row.findViewById(R.id.txtMemo);

            row.setTag(holder);
        } else {
            holder = (RouteStopHolder) row.getTag();
        }

        final RouteStop routeStop = data.get(position);
        holder.nameView.setText(routeStop.getStop().getName());
        if (Math.random() < 0.2) {
            //TODO
            holder.txtMemo.setText("Только по сказочным дням");
        } else {
            holder.txtMemo.setText("");
        }

        return row;
    }

    static class RouteStopHolder {
        TextView nameView;
        TextView txtMemo;
    }
}
