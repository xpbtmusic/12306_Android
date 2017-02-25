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

public class TrainsAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    public static List<Boolean> checkStatus;

    public TrainsAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        initCheckStatus();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = View.inflate(context, R.layout.item_trains, null);
        }
        else {
            view = convertView;
        }
        getCheckStatus(view, position);
        return view;
    }

    private void initCheckStatus() {
        checkStatus = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            checkStatus.add(false);
        }
    }

    private void getCheckStatus(View view, final int position) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.setText(list.get(position));
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
