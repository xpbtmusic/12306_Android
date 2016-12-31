package com.akari.tickets.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.akari.tickets.R;
import com.akari.tickets.adapter.Date2Adapter;
import com.akari.tickets.adapter.PassengersAdapter;
import com.akari.tickets.adapter.SeatsAdapter;
import com.akari.tickets.adapter.TrainsAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TextView choosePassengers;
    private TextView chooseTrains;
    private TextView chooseSeats;
    private TextView chooseDate;
    private TextView chooseDate2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choosePassengers = (TextView) findViewById(R.id.choose_passengers);
        chooseTrains = (TextView) findViewById(R.id.choose_trains);
        chooseSeats = (TextView) findViewById(R.id.choose_seats);
        chooseDate = (TextView) findViewById(R.id.choose_date);
        chooseDate2 = (TextView) findViewById(R.id.choose_date2);
        loadDefaultData();

        choosePassengers.setOnClickListener(this);
        chooseTrains.setOnClickListener(this);
        chooseSeats.setOnClickListener(this);
        chooseDate.setOnClickListener(this);
        chooseDate2.setOnClickListener(this);
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
                break;
            case R.id.choose_date2:
                buildChooseDate2Dialog();
                break;
            default:
                break;
        }
    }

    private void buildChoosePassengersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View view = View.inflate(MainActivity.this, R.layout.choose_passengers, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        final List<String> list = new ArrayList<>();
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
                StringBuilder builder = new StringBuilder();
                boolean first = true;

                for (int i = 0; i < list.size(); i ++) {
                    if (PassengersAdapter.checkStatus.get(i)) {
                        if (first) {
                            builder.append(list.get(i));
                            first = false;
                        }
                        else {
                            builder.append(", ");
                            builder.append(list.get(i));
                        }
                    }
                }
                choosePassengers.setText(builder.toString());
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
        final List<String> list = new ArrayList<>();
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

        builder.setTitle("选择车次");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;

                for (int i = 0; i < list.size(); i ++) {
                    if (TrainsAdapter.checkStatus.get(i)) {
                        if (first) {
                            builder.append(list.get(i));
                            first = false;
                        }
                        else {
                            builder.append(", ");
                            builder.append(list.get(i));
                        }
                    }
                }
                chooseTrains.setText(builder.toString());
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
                StringBuilder builder = new StringBuilder();
                boolean first = true;

                for (int i = 0; i < SeatsAdapter.seats.length; i ++) {
                    if (SeatsAdapter.checkStatus.get(i)) {
                        if (first) {
                            builder.append(SeatsAdapter.seats[i]);
                            first = false;
                        }
                        else {
                            builder.append(", ");
                            builder.append(SeatsAdapter.seats[i]);
                        }
                    }
                }
                chooseSeats.setText(builder.toString());
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
        int month = Integer.parseInt(date.split("-")[1]);
        int day = Integer.parseInt(date.split("-")[2]);
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, this, year, month - 1, day);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        chooseDate.setText(year + "-" + (month + 1) + "-" + day);
    }

    private void buildChooseDate2Dialog() {
        String date = chooseDate.getText().toString();
        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]);
        int day = Integer.parseInt(date.split("-")[2]);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View view = View.inflate(MainActivity.this, R.layout.choose_date2, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(new Date2Adapter(MainActivity.this, year, month, day));

        builder.setTitle("备选日期");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;

                for (int i = 0; i < Date2Adapter.date2.length; i ++) {
                    if (Date2Adapter.checkStatus.get(i)) {
                        if (first) {
                            builder.append(Date2Adapter.date2[i]);
                            first = false;
                        }
                        else {
                            builder.append(", ");
                            builder.append(Date2Adapter.date2[i]);
                        }
                    }
                }
                chooseDate2.setText(builder.toString());
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
