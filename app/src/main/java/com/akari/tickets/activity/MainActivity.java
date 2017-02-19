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

import com.akari.tickets.R;
import com.akari.tickets.adapter.Date2Adapter;
import com.akari.tickets.adapter.PassengersAdapter;
import com.akari.tickets.adapter.SeatsAdapter;
import com.akari.tickets.adapter.TrainsAdapter;
import com.akari.tickets.beans.CheckOrderInfoResponse;
import com.akari.tickets.beans.ConfirmSingleForQueueResponse;
import com.akari.tickets.beans.OrderParam;
import com.akari.tickets.beans.Passenger;
import com.akari.tickets.beans.QueryLeftNewDTO;
import com.akari.tickets.beans.QueryParam;
import com.akari.tickets.beans.QueryTrainsResponse;
import com.akari.tickets.fragment.DatePickerFragment;
import com.akari.tickets.http.HttpService;
import com.akari.tickets.http.RetrofitManager;
import com.akari.tickets.rxbus.RxBus;
import com.akari.tickets.utils.DateUtil;
import com.akari.tickets.utils.OrderUtil;
import com.akari.tickets.utils.PassengerUtil;
import com.akari.tickets.utils.StationCodeUtil;
import com.akari.tickets.utils.SubscriptionUtil;
import com.akari.tickets.utils.ToastUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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

    private Subscription querySubscription;
    private Subscription busSubscription;
    private Subscription loopSubscription;
    private Subscription orderSubscription;
    private String leftTicketUrl = "leftTicket/queryA";
    private static OrderParam orderParam;
    private static boolean breakChooseSeats = false;

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

        registerBus();
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
        getLeftTicketUrlAndGetTrainsFirst();
    }

    private void getLeftTicketUrlAndGetTrainsFirst() {
        final HttpService service = RetrofitManager.getInstance().getService();
        SubscriptionUtil.unSubscribe(querySubscription);
        final QueryParam queryParam = getQueryParam();
        querySubscription = service.getLeftTicketUrl()
                .flatMap(new Func1<ResponseBody, Observable<QueryTrainsResponse>>() {
                    @Override
                    public Observable<QueryTrainsResponse> call(ResponseBody responseBody) {
                        try {
                            leftTicketUrl = responseBody.string().split("CLeftTicketUrl = '")[1].split("';")[0];
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return service.queryTrains(leftTicketUrl, queryParam.getTrain_date(), queryParam.getFrom_station_code(), queryParam.getTo_station_code(), queryParam.getPurpose_codes());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<QueryTrainsResponse>() {
                    @Override
                    public void call(QueryTrainsResponse queryTrainsResponse) {
                        trains.clear();
                        List<QueryTrainsResponse.Data> datas = queryTrainsResponse.getData();
                        for (QueryTrainsResponse.Data data : datas) {
                            trains.add(data.getQueryLeftNewDTO().getStation_train_code());
                        }
                    }
                });
    }

    private void getTrains() {
        HttpService service = RetrofitManager.getInstance().getService();
        SubscriptionUtil.unSubscribe(querySubscription);
        QueryParam queryParam = getQueryParam();
        querySubscription = service.queryTrains(leftTicketUrl, queryParam.getTrain_date(), queryParam.getFrom_station_code(), queryParam.getTo_station_code(), queryParam.getPurpose_codes())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<QueryTrainsResponse>() {
                    @Override
                    public void call(QueryTrainsResponse queryTrainsResponse) {
                        trains.clear();
                        List<QueryTrainsResponse.Data> datas = queryTrainsResponse.getData();
                        for (QueryTrainsResponse.Data data : datas) {
                            trains.add(data.getQueryLeftNewDTO().getStation_train_code());
                        }
                    }
                });
    }

    private void registerBus() {
        SubscriptionUtil.unSubscribe(busSubscription);
        busSubscription = RxBus.getDefault().toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        showShortToast(o.toString());
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
                if (button.getText().toString().equals("开始查询")) {
                    if (preCheckThrough()) {
                        button.setText("停止查询");
                        startQueryLoop();
                    }
                }
                else {
                    SubscriptionUtil.unSubscribe(loopSubscription);
                    button.setText("开始查询");
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
                    showShortToast("请选择席别");
                }
            }
            else {
                showShortToast("请选择车次");
            }
        }
        else {
            showShortToast("请选择乘车人");
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

    private void startQueryLoop() {
        SubscriptionUtil.unSubscribe(loopSubscription);
        SubscriptionUtil.unSubscribe(querySubscription);
        final HttpService service = RetrofitManager.getInstance().getService();
        final QueryParam queryParam = getQueryParam();
        loopSubscription = Observable.interval(1500, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Long, Observable<QueryTrainsResponse>>() {
                    @Override
                    public Observable<QueryTrainsResponse> call(Long aLong) {
                        System.out.println("正在查询..." + queryParam.getFrom_station() + " - " + queryParam.getTo_station() + "..." + (aLong + 1));
                        return service.queryTrains(leftTicketUrl, queryParam.getTrain_date(), queryParam.getFrom_station_code(), queryParam.getTo_station_code(), queryParam.getPurpose_codes());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<QueryTrainsResponse>() {
                    @Override
                    public void call(QueryTrainsResponse queryTrainsResponse) {
                        for (QueryTrainsResponse.Data data : queryTrainsResponse.getData()) {
                            if (!data.getSecretStr().equals("")) {
                                if (data.getQueryLeftNewDTO().getStation_train_code().equals(queryParam.getTrain_code())) {
                                    String[] seats = queryParam.getSeats();
                                    for (String seat : seats) {
                                        querySeats(seat, data.getQueryLeftNewDTO(), data.getSecretStr(), queryParam);
                                        if (breakChooseSeats) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void querySeats(String seat, QueryLeftNewDTO queryLeftNewDTO, String secretStr, QueryParam queryParam) {
        switch (seat) {
            case "软卧":
                if (!queryLeftNewDTO.getRw_num().equals("无") && !queryLeftNewDTO.getRw_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(loopSubscription);
                    button.setText("开始查询");
                    System.out.println("正在抢软卧...");
                    OrderUtil.seat_type_codes = "4";
                    breakChooseSeats = true;
                    submitOrder(secretStr, queryParam);
                }
                break;
            case "硬卧":
                if (!queryLeftNewDTO.getYw_num().equals("无") && !queryLeftNewDTO.getYw_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(loopSubscription);
                    button.setText("开始查询");
                    System.out.println("正在抢硬卧...");
                    OrderUtil.seat_type_codes = "3";
                    breakChooseSeats = true;
                    submitOrder(secretStr, queryParam);
                }
                break;
            case "硬座":
                if (!queryLeftNewDTO.getYz_num().equals("无") && !queryLeftNewDTO.getYz_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(loopSubscription);
                    button.setText("开始查询");
                    System.out.println("正在抢硬座...");
                    OrderUtil.seat_type_codes = "1";
                    breakChooseSeats = true;
                    submitOrder(secretStr, queryParam);
                }
                break;
            case "无座":
                if (!queryLeftNewDTO.getWz_num().equals("无") && !queryLeftNewDTO.getWz_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(loopSubscription);
                    button.setText("开始查询");
                    System.out.println("正在抢无座...");
                    OrderUtil.seat_type_codes = "1";
                    breakChooseSeats = true;
                    submitOrder(secretStr, queryParam);
                }
                break;
            default:
                break;
        }
    }

    private void submitOrder(String secretStr, QueryParam queryParam) {
        final Map<String, String> map = new HashMap<>();
        map.put("secretStr", secretStr);
        map.put("train_date", queryParam.getTrain_date());
        map.put("back_train_date", queryParam.getBack_train_date());
        map.put("tour_flag", "dc");
        map.put("purpose_codes", queryParam.getPurpose_codes());
        map.put("query_from_station_name", queryParam.getFrom_station());
        map.put("query_to_station_name", queryParam.getTo_station());
        map.put("undefined", "");

        final HttpService service = RetrofitManager.getInstance().getService();
        orderSubscription = service.submitOrder(map)
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        return service.initDc("");
                    }
                })
                .flatMap(new Func1<ResponseBody, Observable<CheckOrderInfoResponse>>() {
                    @Override
                    public Observable<CheckOrderInfoResponse> call(ResponseBody responseBody) {
                        try {
                            String response = responseBody.string();
                            String globalRepeatSubmitToken = response.split("globalRepeatSubmitToken = '")[1].split("';")[0];
                            orderParam = new OrderParam();
                            orderParam.setREPEAT_SUBMIT_TOKEN(globalRepeatSubmitToken);

                            JSONObject object = new JSONObject(response.split("ticketInfoForPassengerForm=")[1].split(";")[0]);
                            OrderUtil.getOrderParam(object, orderParam);

                            map.clear();
                            map.put("cancel_flag", "2");
                            map.put("bed_level_order_num", "000000000000000000000000000000");
                            map.put("passengerTicketStr", orderParam.getPassengerTicketStr());
                            map.put("oldPassengerStr", orderParam.getOldPassengerStr());
                            map.put("tour_flag", "dc");
                            map.put("randCode", "");
                            map.put("_json_att", "");
                            map.put("REPEAT_SUBMIT_TOKEN", orderParam.getREPEAT_SUBMIT_TOKEN());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return service.checkOrderInfo(map);
                    }
                })
                .flatMap(new Func1<CheckOrderInfoResponse, Observable<ConfirmSingleForQueueResponse>>() {
                    @Override
                    public Observable<ConfirmSingleForQueueResponse> call(CheckOrderInfoResponse checkOrderInfoResponse) {
                        if (checkOrderInfoResponse.getData().isSubmitStatus()) {
                            map.clear();
                            map.put("passengerTicketStr", orderParam.getPassengerTicketStr());
                            map.put("oldPassengerStr", orderParam.getOldPassengerStr());
                            map.put("randCode", orderParam.getRandCode());
                            map.put("purpose_codes", orderParam.getPurpose_codes());
                            map.put("key_check_isChange", orderParam.getKey_check_isChange());
                            map.put("leftTicketStr", orderParam.getLeftTicketStr());
                            map.put("train_location", orderParam.getTrain_location());
                            map.put("choose_seats", orderParam.getChoose_seats());
                            map.put("seatDetailType", orderParam.getSeatDetailType());
                            map.put("roomType", orderParam.getRoomType());
                            map.put("dwAll", orderParam.getDwAll());
                            map.put("_json_att", orderParam.get_json_att());
                            map.put("REPEAT_SUBMIT_TOKEN", orderParam.getREPEAT_SUBMIT_TOKEN());
                            return service.confirmSingleForQueue(map);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ConfirmSingleForQueueResponse>() {
                    @Override
                    public void call(ConfirmSingleForQueueResponse confirmSingleForQueueResponse) {
                        if (confirmSingleForQueueResponse.getData().isSubmitStatus()) {
                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            Notification notification = new Notification.Builder(MainActivity.this)
                                    .setContentTitle("Tickets")
                                    .setContentText("打开12306看看")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .build();
                            manager.notify(1, notification);
                        }
                    }
                });
    }

    private void showShortToast(String s) {
        ToastUtil.showShortToast(MainActivity.this, s);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionUtil.unSubscribe(busSubscription);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerBus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionUtil.unSubscribe(querySubscription);
        SubscriptionUtil.unSubscribe(busSubscription);
        SubscriptionUtil.unSubscribe(loopSubscription);
        SubscriptionUtil.unSubscribe(orderSubscription);
    }
}
