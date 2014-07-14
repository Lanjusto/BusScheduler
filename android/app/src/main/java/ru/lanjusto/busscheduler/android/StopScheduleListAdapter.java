package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StopScheduleListAdapter extends ArrayAdapter<StopScheduleItem> {
    private final Context context;
    private final int layoutResourceId;
    private final List<StopScheduleItem> data;

    public StopScheduleListAdapter(Context context, int layoutResourceId, List<StopScheduleItem> data) {
        super(context, layoutResourceId, data);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
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
        holder.txtDepartureTime.setText(stopScheduleItem.getDepartureTime().toString());
        holder.txtRestingTime.setText(stopScheduleItem.getRestingTime());
        holder.txtRouteNum.setText(stopScheduleItem.getRouteNum());
        holder.txtDestinationPoint.setText(stopScheduleItem.getDestinationPoint());

        return row;
    }

    private static class StopScheduleItemHolder {
        TextView txtDepartureTime;
        TextView txtRestingTime;
        TextView txtRouteNum;
        TextView txtDestinationPoint;
    }
}
