package com.akari.tickets.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.akari.tickets.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akari on 2016/12/30.
 */

public class SeatsAdapter extends BaseAdapter {

    private Context context;
    public static String[] seats = {"软卧", "硬卧", "硬座", "无座"};
    public static List<Boolean> checkStatus;

    public SeatsAdapter(Context context) {
        this.context = context;
        initCheckStatus();
    }

    @Override
    public int getCount() {
        return seats.length;
    }

    @Override
    public Object getItem(int position) {
        return seats[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = View.inflate(context, R.layout.item_seats, null);
        }
        else {
            view = convertView;
        }
        getCheckStatus(view, position);
        return view;
    }

    private void initCheckStatus() {
        checkStatus = new ArrayList<>();
        for (int i = 0; i < seats.length; i++) {
            checkStatus.add(false);
        }
    }

    private void getCheckStatus(View view, final int position) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.setText(seats[position]);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    checkStatus.set(position, true);
                }
                else {
                    checkStatus.set(position, false);
                }
            }
        });
        checkBox.setChecked(checkStatus.get(position));
    }
}
