package com.akari.tickets.ui.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.akari.tickets.R;
import com.akari.tickets.beans.CheckRandCodeResponse;
import com.akari.tickets.beans.QueryOrderWaitTimeResponse;
import com.akari.tickets.beans.ResultOrderResponse;
import com.akari.tickets.ui.adapter.Date2Adapter;
import com.akari.tickets.ui.adapter.PassengersAdapter;
import com.akari.tickets.ui.adapter.SeatsAdapter;
import com.akari.tickets.ui.adapter.TrainsAdapter;
import com.akari.tickets.beans.CheckOrderInfoResponse;
import com.akari.tickets.beans.ConfirmSingleForQueueResponse;
import com.akari.tickets.beans.OrderParam;
import com.akari.tickets.beans.Passenger;
import com.akari.tickets.beans.QueryLeftNewDTO;
import com.akari.tickets.beans.QueryParam;
import com.akari.tickets.beans.QueryTrainsResponse;
import com.akari.tickets.ui.fragment.DatePickerFragment;
import com.akari.tickets.network.HttpService;
import com.akari.tickets.network.RetrofitManager;
import com.akari.tickets.utils.AlertDialogUtil;
import com.akari.tickets.utils.DateUtil;
import com.akari.tickets.utils.OrderUtil;
import com.akari.tickets.utils.PassengerUtil;
import com.akari.tickets.utils.RandCodeUtil;
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
import java.util.Random;
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
    private ImageView refresh;

    private TextView trainCode;
    private TextView rwNum;
    private TextView ywNum;
    private TextView yzNum;
    private TextView wzNum;
    private TextView queryCount;

    private Subscription querySubscription;
    private Subscription busSubscription;
    private Subscription queryTrainLoopSubscription;
    private Subscription orderSubscription;
    private Subscription getPassCodeSubscription;
    private Subscription queryWaitTimeLoopSubscription;
    private Subscription orderCompleteSubscription;
    private String leftTicketUrl = "leftTicket/queryA";
    private static OrderParam orderParam;
    private static Map<String, String> map;
    private static boolean breakChooseSeats = false;
    private static int count = 0;
    private static Bitmap randCodeImg;
    private static String randCode = "";
    private static boolean showPassCode = false;

    private ImageView imageView;
    private ImageView selected1;
    private ImageView selected2;
    private ImageView selected3;
    private ImageView selected4;
    private ImageView selected5;
    private ImageView selected6;
    private ImageView selected7;
    private ImageView selected8;
    private List<ImageView> list;

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
        refresh = (ImageView) findViewById(R.id.refresh);
        trainCode = (TextView) findViewById(R.id.train_code);
        rwNum = (TextView) findViewById(R.id.rw_num);
        ywNum = (TextView) findViewById(R.id.yw_num);
        yzNum = (TextView) findViewById(R.id.yz_num);
        wzNum = (TextView) findViewById(R.id.wz_num);
        queryCount = (TextView) findViewById(R.id.query_count);
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

//        registerBus();
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

//    private void registerBus() {
//        SubscriptionUtil.unSubscribe(busSubscription);
//        busSubscription = RxBus.getDefault().toObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Object>() {
//                    @Override
//                    public void call(Object o) {
//                        showShortToast(o.toString());
//                    }
//                });
//    }

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
                    count = 0;
                    if (preCheckThrough()) {
                        button.setText("停止查询");
                        startQueryLoop();
                    }
                }
                else {
                    SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
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
        AlertDialogUtil.setButton(builder, new DialogInterface.OnClickListener() {
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
        builder.show();
    }

    private void buildChooseTrainsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.choose_trains, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(new TrainsAdapter(MainActivity.this, trains));

        builder.setTitle("选择车次");
        builder.setView(view);
        AlertDialogUtil.setButton(builder, new DialogInterface.OnClickListener() {
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
        builder.show();
    }

    private void buildChooseSeatsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.choose_seats, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(new SeatsAdapter(MainActivity.this));

        builder.setTitle("选择席别");
        builder.setView(view);
        AlertDialogUtil.setButton(builder, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for (int i = 0; i < SeatsAdapter.list.size(); i ++) {
                    if (SeatsAdapter.checkStatus.get(i)) {
                        if (first) {
                            builder.append(SeatsAdapter.list.get(i));
                            first = false;
                        }
                        else {
                            builder.append(", ");
                            builder.append(SeatsAdapter.list.get(i));
                        }
                    }
                }
                chooseSeats.setText(builder.toString());
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
        AlertDialogUtil.setButton(builder, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for (int i = 0; i < Date2Adapter.list.size(); i ++) {
                    if (Date2Adapter.checkStatus.get(i)) {
                        if (first) {
                            builder.append(Date2Adapter.list.get(i));
                            first = false;
                        }
                        else {
                            builder.append(", ");
                            builder.append(Date2Adapter.list.get(i));
                        }
                    }
                }
                chooseDate2.setText(builder.toString());
            }
        });
        builder.show();
    }

    private void startQueryLoop() {
        SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
        SubscriptionUtil.unSubscribe(querySubscription);
        final HttpService service = RetrofitManager.getInstance().getService();
        final QueryParam queryParam = getQueryParam();
        String[] trainDates;
        if (!queryParam.getDate2()[0].equals("")) {
            trainDates = new String[queryParam.getDate2().length + 1];
            trainDates[0] = queryParam.getTrain_date();
            for (int i = 1; i < trainDates.length; i++) {
                trainDates[i] = queryParam.getDate2()[i - 1];
            }
        }
        else {
            trainDates = new String[1];
            trainDates[0] = queryParam.getTrain_date();
        }
        queryTrainLoopSubscription = Observable.interval(1500, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Long, Observable<QueryTrainsResponse>>() {
                    @Override
                    public Observable<QueryTrainsResponse> call(Long aLong) {
                        String trainDate = queryParam.getTrain_date();
                        if (!queryParam.getDate2()[0].equals("")) {
                            long i = aLong % (queryParam.getDate2().length + 1);
                            if (i == queryParam.getDate2().length) {
                                trainDate = queryParam.getTrain_date();
                            }
                            else {
                                trainDate = queryParam.getDate2()[(int)i];
                            }
                        }
                        return service.queryTrains(leftTicketUrl, trainDate, queryParam.getFrom_station_code(), queryParam.getTo_station_code(), queryParam.getPurpose_codes());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<QueryTrainsResponse>() {
                    @Override
                    public void call(QueryTrainsResponse queryTrainsResponse) {
                        queryCount.setText("第" + ++count + "次查询");
                        for (QueryTrainsResponse.Data data : queryTrainsResponse.getData()) {
                            if (data.getQueryLeftNewDTO().getStation_train_code().equals(chooseTrains.getText().toString())) {
                                trainCode.setText(chooseTrains.getText().toString());
                                rwNum.setText(data.getQueryLeftNewDTO().getRw_num());
                                ywNum.setText(data.getQueryLeftNewDTO().getYw_num());
                                yzNum.setText(data.getQueryLeftNewDTO().getYz_num());
                                wzNum.setText(data.getQueryLeftNewDTO().getWz_num());
                            }
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
                    SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
                    button.setText("开始查询");
                    OrderUtil.seat_type_codes = "4";
                    breakChooseSeats = true;
                    submitOrder(secretStr, queryParam);
                }
                break;
            case "硬卧":
                if (!queryLeftNewDTO.getYw_num().equals("无") && !queryLeftNewDTO.getYw_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
                    button.setText("开始查询");
                    OrderUtil.seat_type_codes = "3";
                    breakChooseSeats = true;
                    submitOrder(secretStr, queryParam);
                }
                break;
            case "硬座":
                if (!queryLeftNewDTO.getYz_num().equals("无") && !queryLeftNewDTO.getYz_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
                    button.setText("开始查询");
                    OrderUtil.seat_type_codes = "1";
                    breakChooseSeats = true;
                    submitOrder(secretStr, queryParam);
                }
                break;
            case "无座":
                if (!queryLeftNewDTO.getWz_num().equals("无") && !queryLeftNewDTO.getWz_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
                    button.setText("开始查询");
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
        map = new HashMap<>();
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
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
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
                        return service.getPassCode("passenger", "randp");
                    }
                })
                .flatMap(new Func1<ResponseBody, Observable<CheckOrderInfoResponse>>() {
                    @Override
                    public Observable<CheckOrderInfoResponse> call(ResponseBody responseBody) {
                        randCodeImg = BitmapFactory.decodeStream(responseBody.byteStream());
                        return service.checkOrderInfo(map);
                    }
                })
                .map(new Func1<CheckOrderInfoResponse, Boolean>() {
                    @Override
                    public Boolean call(CheckOrderInfoResponse checkOrderInfoResponse) {
                        CheckOrderInfoResponse.Data data = checkOrderInfoResponse.getData();
                        if (data.isSubmitStatus()) {
                            if (data.getIfShowPassCode().equals("Y")) {
                                showPassCode = true;
                            }
                        }
                        else {
                            showLongToast(data.getErrMsg());
                        }
                        return showPassCode;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean showCode) {
                        if (showCode) {
                            showPassCode = false;
                            buildPassCodeDialog();
                        }
                        else {
                            submitOrderNext(service, orderParam.getREPEAT_SUBMIT_TOKEN());
                        }
                    }
                });
    }

    private void buildPassCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.show_passcode, null);
        imageView = (ImageView) view.findViewById(R.id.passcode);
        selected1 = (ImageView) view.findViewById(R.id.selected1);
        selected2 = (ImageView) view.findViewById(R.id.selected2);
        selected3 = (ImageView) view.findViewById(R.id.selected3);
        selected4 = (ImageView) view.findViewById(R.id.selected4);
        selected5 = (ImageView) view.findViewById(R.id.selected5);
        selected6 = (ImageView) view.findViewById(R.id.selected6);
        selected7 = (ImageView) view.findViewById(R.id.selected7);
        selected8 = (ImageView) view.findViewById(R.id.selected8);
        addToList();

        imageView.setImageBitmap(randCodeImg);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX() / LoginActivity.density;
                    float y = event.getY() / LoginActivity.density - 33;
                    RandCodeUtil.changeSelectedStatus(x, y, list, false);
                }
                return false;
            }
        });

        builder.setTitle("选择验证码");
        builder.setCancelable(false);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(RandCodeUtil.getRandCode(list))) {
                    showShortToast("请点击验证码进行验证");
                }
                else {
                    randCode = RandCodeUtil.getRandCode(list);
                    checkRandCodeAndNext();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void refreshPassCode() {
        HttpService service = RetrofitManager.getInstance().getService();
        getPassCodeSubscription = service.getPassCode("passenger", "randp")
                .map(new Func1<ResponseBody, Bitmap>() {
                    @Override
                    public Bitmap call(ResponseBody responseBody) {
                        return BitmapFactory.decodeStream(responseBody.byteStream());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    private void submitOrderNext(final HttpService service, final String token) {
        orderParam.setRandCode(randCode);
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
        service.confirmSingleForQueue(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ConfirmSingleForQueueResponse>() {
                    @Override
                    public void call(ConfirmSingleForQueueResponse confirmSingleForQueueResponse) {
                        if (confirmSingleForQueueResponse.getData().isSubmitStatus()) {
                            queryOrderWaitTimeLoop(service, token);
                        }
                    }
                });
    }

    private void queryOrderWaitTimeLoop(final HttpService service, final String token) {
        SubscriptionUtil.unSubscribe(queryWaitTimeLoopSubscription);
        queryWaitTimeLoopSubscription = Observable.interval(200, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Long, Observable<QueryOrderWaitTimeResponse>>() {
                    @Override
                    public Observable<QueryOrderWaitTimeResponse> call(Long aLong) {
                        return service.queryOrderWaitTime(getRandom(), "dc", "", token);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<QueryOrderWaitTimeResponse>() {
                    @Override
                    public void call(QueryOrderWaitTimeResponse queryOrderWaitTimeResponse) {
                        QueryOrderWaitTimeResponse.Data data = queryOrderWaitTimeResponse.getData();
                        if (data.getWaitTime() < 0) {
                            submitOrderCompleted(service, data.getOrderId(), token);
                        }
                    }
                });
    }

    private void submitOrderCompleted(HttpService service, String orderId, String token) {
        SubscriptionUtil.unSubscribe(queryWaitTimeLoopSubscription);
        orderCompleteSubscription = service.resultOrderForDcQueue(orderId, "", token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultOrderResponse>() {
                    @Override
                    public void call(ResultOrderResponse resultOrderResponse) {
                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        Notification notification = new Notification.Builder(MainActivity.this)
                                .setContentTitle("Tickets")
                                .setContentText("打开12306看看")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .build();
                        manager.notify(1, notification);
                    }
                });
    }

    private String getRandom() {
        String s = new Random().nextDouble() + "";
        return s.split("\\.")[1].substring(0, 13);
    }

    private void addToList() {
        list = new ArrayList<>();
        list.add(selected1);
        list.add(selected2);
        list.add(selected3);
        list.add(selected4);
        list.add(selected5);
        list.add(selected6);
        list.add(selected7);
        list.add(selected8);
    }

    private void checkRandCodeAndNext() {
        final HttpService service = RetrofitManager.getInstance().getService();
        service.checkRandCode2(randCode, "randp", "", orderParam.getREPEAT_SUBMIT_TOKEN())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CheckRandCodeResponse>() {
                    @Override
                    public void call(CheckRandCodeResponse checkRandCodeResponse) {
                        if (checkRandCodeResponse.getData().getResult().equals("1")) {
                            submitOrderNext(service, orderParam.getREPEAT_SUBMIT_TOKEN());
                        }
                        else {
                            showShortToast("验证码错误");
                            RandCodeUtil.clearSelected(list);
                            refreshPassCode();
                        }
                    }
                });
    }

    private void showShortToast(String s) {
        ToastUtil.showShortToast(MainActivity.this, s);
    }

    private void showLongToast(String s) {
        ToastUtil.showLongToast(MainActivity.this, s);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SubscriptionUtil.unSubscribe(querySubscription);
        SubscriptionUtil.unSubscribe(orderSubscription);
        SubscriptionUtil.unSubscribe(orderCompleteSubscription);
        SubscriptionUtil.unSubscribe(getPassCodeSubscription);
        SubscriptionUtil.unSubscribe(busSubscription);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        registerBus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
