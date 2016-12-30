package com.akari.tickets.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akari.tickets.R;
import com.akari.tickets.adapter.PassengersAdapter;
import com.akari.tickets.adapter.SeatsAdapter;
import com.akari.tickets.adapter.TrainsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView choosePassengers;
    private TextView chooseTrains;
    private TextView chooseSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choosePassengers = (TextView) findViewById(R.id.choose_passengers);
        chooseTrains = (TextView) findViewById(R.id.choose_trains);
        chooseSeats = (TextView) findViewById(R.id.choose_seats);

        choosePassengers.setOnClickListener(this);
        chooseTrains.setOnClickListener(this);
        chooseSeats.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_passengers:
                buildChoosePassengersDialog();
                break;
            case R.id.choose_trains:
                buildChooseTrainsDialog();
                break;
            case R.id.choose_seats:
                buildChooseSeatsDialog();
                break;
            default:
                break;
        }
    }

    private void buildChoosePassengersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View view = View.inflate(MainActivity.this, R.layout.choose_passengers, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        List<String> list = new ArrayList<>();
        list.add("交互");
        list.add("三等功");
        list.add("任何人");
        list.add("多少");
        list.add("热二天");
        listView.setAdapter(new PassengersAdapter(MainActivity.this, list));

        builder.setTitle("选择乘车人");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println(PassengersAdapter.checkStatus);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void buildChooseTrainsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View view = View.inflate(MainActivity.this, R.layout.choose_trains, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        List<String> list = new ArrayList<>();
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        list.add("T368");
        listView.setAdapter(new TrainsAdapter(MainActivity.this, list));

        builder.setTitle("选择席别");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println(TrainsAdapter.checkStatus);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void buildChooseSeatsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View view = View.inflate(MainActivity.this, R.layout.choose_seats, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(new SeatsAdapter(MainActivity.this));

        builder.setTitle("选择席别");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println(SeatsAdapter.checkStatus);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
