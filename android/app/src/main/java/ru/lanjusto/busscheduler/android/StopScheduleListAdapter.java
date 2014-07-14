package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.lanjusto.busscheduler.common.model.Time;

import java.util.ArrayList;
import java.util.List;

public class StopScheduleListAdapter extends ArrayAdapter<StopScheduleItem> {
    private final Context context;
    private final int layoutResourceId;
    private final List<StopScheduleItem> data;
    private final List<View> views;

    public StopScheduleListAdapter(Context context, int layoutResourceId, List<StopScheduleItem> data) {
        super(context, layoutResourceId, data);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
        this.views = new ArrayList<View>();
    }

    void updateRestingTime(Time currentTime) {
        for (View item : views) {
            final StopScheduleItemHolder holder = (StopScheduleItemHolder) item.getTag();
            final Time delta = holder.departureTime.substract(currentTime);

            if (delta.isZero()) {
                holder.isLeft = true;
            }

            final String text;
            final Integer color;
            if (delta.isZero()) {
                text = "уходит сейчас";
                color = null;
            } else if (holder.isLeft) {
                color = R.color.light_gray;
                text = "ушёл " + Time.inverse(delta).toHumanString() + " назад";
            } else {
                color = null;
                text = "через " + delta.toHumanString();
            }
            ((TextView) item.findViewById(R.id.restingTime)).setText(text);

            if (color != null) {
                holder.txtDepartureTime.setTextColor(color);
                holder.txtRestingTime.setTextColor(color);
                holder.txtRouteNum.setTextColor(color);
                holder.txtDestinationPoint.setTextColor(color);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final StopScheduleItemHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StopScheduleItemHolder();
            holder.txtDepartureTime = (TextView) row.findViewById(R.id.departureTime);
            holder.txtRestingTime = (TextView) row.findViewById(R.id.restingTime);
            holder.txtRouteNum = (TextView) row.findViewById(R.id.routeNum);
            holder.txtDestinationPoint = (TextView) row.findViewById(R.id.destinationPoint);

            row.setTag(holder);
        } else {
            holder = (StopScheduleItemHolder) row.getTag();
        }

        final StopScheduleItem stopScheduleItem = data.get(position);
        holder.departureTime = stopScheduleItem.getDepartureTime();
        holder.txtDepartureTime.setText(stopScheduleItem.getDepartureTime().toString());
        holder.txtRestingTime.setText("");
        holder.txtRouteNum.setText(stopScheduleItem.getRouteNum());
        holder.txtDestinationPoint.setText(stopScheduleItem.getDestinationPoint());

        views.add(row);
        return row;
    }

    private static class StopScheduleItemHolder {
        Time departureTime;
        boolean isLeft = false;
        TextView txtDepartureTime;
        TextView txtRestingTime;
        TextView txtRouteNum;
        TextView txtDestinationPoint;
    }
}
