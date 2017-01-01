package com.akari.tickets.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akari.tickets.R;

/**
 * Created by Akari on 2016/12/31.
 */

public class StationAdapter1 extends RecyclerView.Adapter<StationAdapter1.ViewHolder> {

    private String[] list = {"的萨芬", "佛挡杀佛", "第三附", "属阿萨", "德地方", "的萨芬", "佛挡杀佛", "第三附", "属阿萨", "德地方", "的萨芬", "佛挡杀佛", "第三附", "属阿萨", "德地方", "的萨芬", "佛挡杀佛", "第三附", "属阿萨", "德地方", "的萨芬", "佛挡杀佛", "第三附", "属阿萨", "德地方"};

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.text_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_station, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(list[position]);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }
}
