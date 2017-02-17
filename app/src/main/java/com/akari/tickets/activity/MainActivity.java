package com.akari.tickets.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akari.tickets.R;
import com.akari.tickets.adapter.Date2Adapter;
import com.akari.tickets.adapter.PassengersAdapter;
import com.akari.tickets.adapter.SeatsAdapter;
import com.akari.tickets.adapter.TrainsAdapter;
import com.akari.tickets.beans.Passenger;
import com.akari.tickets.beans.QueryParam;
import com.akari.tickets.fragment.DatePickerFragment;
import com.akari.tickets.http.APIService;
import com.akari.tickets.http.RetrofitManager;
import com.akari.tickets.utils.DateUtil;
import com.akari.tickets.utils.PassengerUtil;
import com.akari.tickets.utils.QueryUtil;
import com.akari.tickets.utils.StationCodeUtil;
import com.akari.tickets.utils.SubscriptionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
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
    private static String back_strain_date;
    private TextView logText;
    private ScrollView scrollView;
    private ImageView refresh;

    private Subscription subscription;

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
        logText = (TextView) findViewById(R.id.log);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        refresh = (ImageView) findViewById(R.id.refresh);
        loadDefaultData();

        fromStation.setOnClickListener(this);
        toStation.setOnClickListener(this);
        choosePassengers.setOnClickListener(this);
        chooseTrains.setOnClickListener(this);
        chooseSeats.setOnClickListener(this);
        chooseDate.setOnClickListener(this);
        chooseDate2.setOnClickListener(this);
        button.setOnClickListener(this);
        refresh.setOnClickListener(this);

        checkIfGet();
    }

    private void loadDefaultData() {
        choosePassengers.setText(PassengerUtil.getUserSelf().getPassenger_name());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateStr = DateUtil.getDateStr(year, month, day);
        chooseDate.setText(dateStr);
        back_strain_date = dateStr;

        trains = new ArrayList<>();
        getTrains();
    }

    private void getTrains() {
        APIService service = RetrofitManager.getInstance().getService();
        SubscriptionUtil.unSubscribe(subscription);
        QueryParam queryParam = getQueryParam();
        subscription = service.queryTrains(queryParam.getTrain_date(), queryParam.getFrom_station_code(), queryParam.getTo_station_code(), queryParam.getPurpose_codes())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string();
                            System.out.println(json);
                            JSONArray array = new JSONObject(json).getJSONArray("data");
                            trains.clear();
                            if (array.length() != 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    trains.add(array.getJSONObject(i).getJSONObject("queryLeftNewDTO").getString("station_train_code"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                    if (button.getText().toString().equals("开始查询")) {
                        QueryUtil.get = false;
                        button.setText("停止查询");
                        QueryUtil.startQueryLoop(getQueryParam());
                        getLog();
                    }
                    else {
                        QueryUtil.get = true;
                        button.setText("开始查询");
                        QueryUtil.thread = null;
                    }
                }
                break;
            case R.id.refresh:
                getTrains();
                break;
            default:
                break;
        }
    }

    private boolean preCheckThrough() {
        if (!TextUtils.isEmpty(choosePassengers.getText().toString())) {
            if (!TextUtils.isEmpty(chooseTrains.getText().toString())) {
                if (!TextUtils.isEmpty(chooseSeats.getText().toString())) {
                    PassengerUtil.selectedPassenger = choosePassengers.getText().toString();
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
                    getTrains();
                }
                break;
            case GET_TO_STATION:
                if (resultCode == RESULT_OK) {
                    toStation.setText(data.getStringExtra("station"));
                    chooseTrains.setText("");
                    chooseSeats.setText("");
                    getTrains();
                }
                break;
            default:
                break;
        }
    }

    private QueryParam getQueryParam() {
        QueryParam queryParam = new QueryParam();
        queryParam.setFrom_station_code(StationCodeUtil.getName2CodeMap().get(fromStation.getText().toString()));
        queryParam.setTo_station_code(StationCodeUtil.getName2CodeMap().get(toStation.getText().toString()));
        queryParam.setFrom_station(fromStation.getText().toString());
        queryParam.setTo_station(toStation.getText().toString());
        queryParam.setTrain_code(chooseTrains.getText().toString());
        queryParam.setTrain_date(chooseDate.getText().toString());
        queryParam.setBack_train_date(back_strain_date);
        queryParam.setSeats(chooseSeats.getText().toString().split(", "));
        queryParam.setDate2(chooseDate2.getText().toString().split(", "));
        queryParam.setPurpose_codes(PassengerUtil.getPassenger(choosePassengers.getText().toString().split(",")[0]).getPassenger_type_name());
        queryParam.setUrl("https://kyfw.12306.cn/otn/leftTicket/queryX?leftTicketDTO.train_date=" + queryParam.getTrain_date() + "&leftTicketDTO.from_station=" + queryParam.getFrom_station_code()
                + "&leftTicketDTO.to_station=" + queryParam.getTo_station_code() + "&purpose_codes=" + queryParam.getPurpose_codes());

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
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
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

    private void getLog() {
        new Thread() {
            @Override
            public void run() {
                while (!QueryUtil.end) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!QueryUtil.log.contains("正在抢")) {
                                logText.append(QueryUtil.log);
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                        if (button.getText().toString().equals("开始查询")) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void checkIfGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!QueryUtil.end) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new Notification.Builder(MainActivity.this)
                        .setContentTitle("Tickets")
                        .setContentText("打开12306看看")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .build();
                manager.notify(1, notification);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionUtil.unSubscribe(subscription);
    }
}
