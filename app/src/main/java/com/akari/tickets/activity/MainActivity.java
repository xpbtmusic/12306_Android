package com.akari.tickets.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akari.tickets.R;
import com.akari.tickets.adapter.Date2Adapter;
import com.akari.tickets.adapter.PassengersAdapter;
import com.akari.tickets.adapter.SeatsAdapter;
import com.akari.tickets.adapter.TrainsAdapter;
import com.akari.tickets.beans.Passenger;
import com.akari.tickets.beans.QueryParam;
import com.akari.tickets.utils.DateUtil;
import com.akari.tickets.utils.HttpUtil;
import com.akari.tickets.utils.PassengerUtil;
import com.akari.tickets.utils.QueryUtil;
import com.akari.tickets.utils.StationCodeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TextView fromStation;
    private TextView toStation;
    private TextView choosePassengers;
    private TextView chooseTrains;
    private TextView chooseSeats;
    private TextView chooseDate;
    private TextView chooseDate2;
    private Button button;
    private static final int GET_FROM_STATION = 1;
    private static final int GET_TO_STATION = 2;
    private static List<String> trains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromStation = (TextView) findViewById(R.id.from_station);
        toStation = (TextView) findViewById(R.id.to_station);
        choosePassengers = (TextView) findViewById(R.id.choose_passengers);
        chooseTrains = (TextView) findViewById(R.id.choose_trains);
        chooseSeats = (TextView) findViewById(R.id.choose_seats);
        chooseDate = (TextView) findViewById(R.id.choose_date);
        chooseDate2 = (TextView) findViewById(R.id.choose_date2);
        button = (Button) findViewById(R.id.button);
        loadDefaultData();

        fromStation.setOnClickListener(this);
        toStation.setOnClickListener(this);
        choosePassengers.setOnClickListener(this);
        chooseTrains.setOnClickListener(this);
        chooseSeats.setOnClickListener(this);
        chooseDate.setOnClickListener(this);
        chooseDate2.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    private void loadDefaultData() {
        choosePassengers.setText(PassengerUtil.getUserSelf().getPassenger_name());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateStr = DateUtil.getDateStr(year, month, day);
        chooseDate.setText(dateStr);

        trains = new ArrayList<>();
        HttpUtil.get(getQueryParam().getUrl(), new GetTrainCodeCallBack());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.from_station:
                startActivityForResult(new Intent(MainActivity.this, ChooseStationActivity.class), GET_FROM_STATION);
                break;
            case R.id.to_station:
                startActivityForResult(new Intent(MainActivity.this, ChooseStationActivity.class), GET_TO_STATION);
                break;
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
            case R.id.button:
                if (preCheckThrough()) {
                    Toast.makeText(MainActivity.this, "开始查询", Toast.LENGTH_SHORT).show();
                    QueryUtil.startQueryLoop(getQueryParam());
                }
                break;
            default:
                break;
        }
    }

    private boolean preCheckThrough() {
        if (!TextUtils.isEmpty(choosePassengers.getText().toString())) {
            if (!TextUtils.isEmpty(chooseTrains.getText().toString())) {
                if (!TextUtils.isEmpty(chooseSeats.getText().toString())) {
                    return true;
                }
                else {
                    Toast.makeText(this, "请选择席别", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "请选择车次", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "请选择乘车人", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_FROM_STATION:
                if (resultCode == RESULT_OK) {
                    fromStation.setText(data.getStringExtra("station"));
                    chooseTrains.setText("");
                    chooseSeats.setText("");
                    HttpUtil.get(getQueryParam().getUrl(), new GetTrainCodeCallBack());
                }
                break;
            case GET_TO_STATION:
                if (resultCode == RESULT_OK) {
                    toStation.setText(data.getStringExtra("station"));
                    chooseTrains.setText("");
                    chooseSeats.setText("");
                    HttpUtil.get(getQueryParam().getUrl(), new GetTrainCodeCallBack());
                }
                break;
            default:
                break;
        }
    }

    private QueryParam getQueryParam() {
        QueryParam queryParam = new QueryParam();
        queryParam.setFrom_station(StationCodeUtil.getName2CodeMap().get(fromStation.getText().toString()));
        queryParam.setTo_station(StationCodeUtil.getName2CodeMap().get(toStation.getText().toString()));
        queryParam.setTrain_code(chooseTrains.getText().toString());
        queryParam.setTrain_date(chooseDate.getText().toString());
        queryParam.setSeats(chooseSeats.getText().toString().split(", "));
        queryParam.setPurpose_codes(PassengerUtil.getPassenger(choosePassengers.getText().toString().split(",")[0]).getPassenger_type_name());
        queryParam.setUrl("https://kyfw.12306.cn/otn/leftTicket/queryA?leftTicketDTO.train_date=" + queryParam.getTrain_date() + "&leftTicketDTO.from_station=" + queryParam.getFrom_station()
                + "&leftTicketDTO.to_station=" + queryParam.getTo_station() + "&purpose_codes=" + queryParam.getPurpose_codes());

        return queryParam;
    }

    private void buildChoosePassengersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View view = View.inflate(MainActivity.this, R.layout.choose_passengers, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);

        List<Passenger> passengers = PassengerUtil.getPassengers();
        final List<String> list = new ArrayList<>();
        for (Passenger p : passengers) {
            list.add(p.getPassenger_name());
        }

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
        listView.setAdapter(new TrainsAdapter(MainActivity.this, trains));

        builder.setTitle("选择车次");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;

                for (int i = 0; i < trains.size(); i ++) {
                    if (TrainsAdapter.checkStatus.get(i)) {
                        if (first) {
                            builder.append(trains.get(i));
                            first = false;
                        }
                        else {
                            builder.append(", ");
                            builder.append(trains.get(i));
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
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (long) 29 * 24 * 60 * 60 * 1000);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String dateStr = DateUtil.getDateStr(year, month, day);
        chooseDate.setText(dateStr);
        HttpUtil.get(getQueryParam().getUrl(), new GetTrainCodeCallBack());
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

    class GetTrainCodeCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            System.out.println(json);
            try {
                JSONArray array = new JSONObject(json).getJSONArray("data");
                trains.clear();
                if (array.length() != 0) {
                    for (int i = 0; i < array.length(); i++) {
                        trains.add(array.getJSONObject(i).getJSONObject("queryLeftNewDTO").getString("station_train_code"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
