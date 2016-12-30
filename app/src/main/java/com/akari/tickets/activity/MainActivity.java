package com.akari.tickets.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.akari.tickets.R;
import com.akari.tickets.adapter.PassengersAdapter;
import com.akari.tickets.adapter.SeatsAdapter;
import com.akari.tickets.adapter.TrainsAdapter;
import com.akari.tickets.fragment.DatePickerFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TextView choosePassengers;
    private TextView chooseTrains;
    private TextView chooseSeats;
    private TextView chooseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choosePassengers = (TextView) findViewById(R.id.choose_passengers);
        chooseTrains = (TextView) findViewById(R.id.choose_trains);
        chooseSeats = (TextView) findViewById(R.id.choose_seats);
        chooseDate = (TextView) findViewById(R.id.choose_date);
        loadDefaultData();

        choosePassengers.setOnClickListener(this);
        chooseTrains.setOnClickListener(this);
        chooseSeats.setOnClickListener(this);
        chooseDate.setOnClickListener(this);
    }

    private void loadDefaultData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseDate.setText(year + "-" + (month + 1) + "-" + day);
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
            case R.id.choose_date:
                buildChooseDateDialog();
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

    private void buildChooseDateDialog() {
//        DialogFragment fragment = new DatePickerFragment();
//        fragment.show(getSupportFragmentManager(), "datePicker");
        String date = chooseDate.getText().toString();
        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]) - 1;
        int day = Integer.parseInt(date.split("-")[2]);
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, this, year, month, day);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        chooseDate.setText(year + "-" + (month + 1) + "-" + day);
    }
}
