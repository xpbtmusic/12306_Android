package com.akari.tickets.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.akari.tickets.R;
import com.akari.tickets.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Akari on 2016/12/31.
 */

public class Date2Adapter extends BaseAdapter {

    private Context context;
    public static String[] date2 = new String[4];
    public static List<Boolean> checkStatus;

    public Date2Adapter(Context context, int year, int month, int day) {
        this.context = context;
        initData(year, month, day);
    }

    private void initData(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day - 2);
        date2[0] = DateUtil.getDateStr(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(year, month - 1, day - 1);
        date2[1] = DateUtil.getDateStr(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(year, month - 1, day + 1);
        date2[2] = DateUtil.getDateStr(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(year, month - 1, day + 2);
        date2[3] = DateUtil.getDateStr(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        checkStatus = new ArrayList<>();
        for (int i = 0; i < date2.length; i++) {
            checkStatus.add(false);
        }
    }

    @Override
    public int getCount() {
        return date2.length;
    }

    @Override
    public Object getItem(int position) {
        return date2[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = View.inflate(context, R.layout.item_date2, null);
        }
        else {
            view = convertView;
        }
        getCheckStatus(view, position);
        return view;
    }

    private void getCheckStatus(View view, final int position) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.setText(date2[position]);
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
