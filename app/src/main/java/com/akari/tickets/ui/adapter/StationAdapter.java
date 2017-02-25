package com.akari.tickets.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.akari.tickets.R;
import com.akari.tickets.utils.constants.Constants;

/**
 * Created by Akari on 2017/1/1.
 */

public class StationAdapter extends BaseAdapter {

    private String[] nameList = Constants.STATION_NAMES.split(",");
    private Context context;

    public StationAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return nameList.length;
    }

    @Override
    public Object getItem(int position) {
        return nameList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.item_station, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textView.setText(nameList[position]);
        return view;
    }

    private class ViewHolder {
        TextView textView;
    }
}
