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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akari.tickets.R;
import com.akari.tickets.beans.CheckRandCodeResponse;
import com.akari.tickets.beans.LoopResponse;
import com.akari.tickets.beans.QueryOrderWaitTimeResponse;
import com.akari.tickets.beans.ResultOrderResponse;
import com.akari.tickets.beans.SubmitOrderResponse;
import com.akari.tickets.rxbus.RxBus;
import com.akari.tickets.service.LoopService;
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
    private ProgressBar progressBar;
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
    private Subscription progressbarBus;
    private String leftTicketUrl = "leftTicket/queryA";
    private static int count = 0;
    private static Bitmap randCodeImg;
    private static String randCode = "";
    private static boolean refreshPassCode = false;

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

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
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

        registerBus();
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
        progressBar.setVisibility(View.VISIBLE);
        HttpService service = RetrofitManager.getInstance().getService();
        SubscriptionUtil.unSubscribe(querySubscription);
        QueryParam queryParam = getQueryParam();
        querySubscription = service.queryTrains(leftTicketUrl, queryParam.getTrain_date(), queryParam.getFrom_station_code(), queryParam.getTo_station_code(), queryParam.getPurpose_codes())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<QueryTrainsResponse>() {
                    @Override
                    public void call(QueryTrainsResponse queryTrainsResponse) {
                        RxBus.getDefault().post("stop");
                        trains.clear();
                        List<QueryTrainsResponse.Data> datas = queryTrainsResponse.getData();
                        for (QueryTrainsResponse.Data data : datas) {
                            trains.add(data.getQueryLeftNewDTO().getStation_train_code());
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
                if (button.getText().toString().equals("开始查询")) {
                    count = 0;
                    if (preCheckThrough()) {
                        button.setText("停止查询");
                        Intent intent = new Intent(MainActivity.this, LoopService.class);
                        intent.putExtra("leftTicketUrl", leftTicketUrl);
                        intent.putExtra("queryParam", getQueryParam());
                        startService(intent);
                    }
                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    stopService(new Intent(MainActivity.this, LoopService.class));
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
        List<Passenger> passengers = PassengerUtil.getPassengers();
        final List<String> list = new ArrayList<>();
        for (Passenger p : passengers) {
            list.add(p.getPassenger_name());
        }

        View view = View.inflate(MainActivity.this, R.layout.choose_passengers, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        PassengersAdapter adapter = new PassengersAdapter(MainActivity.this, list);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择乘车人");
        builder.setView(view);
        AlertDialogUtil.setButton(builder, adapter, choosePassengers);
        builder.show();
    }

    private void buildChooseTrainsDialog() {
        View view = View.inflate(MainActivity.this, R.layout.choose_trains, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        TrainsAdapter adapter = new TrainsAdapter(MainActivity.this, trains);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择车次");
        builder.setView(view);
        AlertDialogUtil.setButton(builder, adapter, chooseTrains);
        builder.show();
    }

    private void buildChooseSeatsDialog() {
        View view = View.inflate(MainActivity.this, R.layout.choose_seats, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        SeatsAdapter adapter = new SeatsAdapter(MainActivity.this);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择席别");
        builder.setView(view);
        AlertDialogUtil.setButton(builder, adapter, chooseSeats);
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

        View view = View.inflate(MainActivity.this, R.layout.choose_date2, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        Date2Adapter adapter = new Date2Adapter(MainActivity.this, year, month, day);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("备选日期");
        builder.setView(view);
        AlertDialogUtil.setButton(builder, adapter, chooseDate2);
        builder.show();
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
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog = builder.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(RandCodeUtil.getRandCode(list))) {
                    showShortToast("请点击验证码进行验证");
                }
                else {
                    randCode = RandCodeUtil.getRandCode(list);
                    RxBus.getDefault().post(randCode);
                }
            }
        });
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

    private void registerBus() {
        SubscriptionUtil.unSubscribe(progressbarBus);
        progressbarBus = RxBus.getDefault().toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o.toString().equals("start")) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        else if (o.toString().equals("stop")) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        else if (o.toString().equals("getTrains")) {
                            getTrains();
                        }
                        else if (o.toString().equals("count")) {
                            queryCount.setText("第" + ++count + "次查询");
                        }
                        else if (o.toString().equals("button")) {
                            button.setText("开始查询");
                        }
                        else if (o.toString().equals("clear")) {
                            RandCodeUtil.clearSelected(list);
                        }
                        else if (o.toString().equals("refreshPassCode")) {
                            refreshPassCode = true;
                        }
                        else if (o.toString().equals("pass")) {
                            alertDialog.dismiss();
                        }
                        else if (o instanceof Bitmap) {
                            if (refreshPassCode) {
                                imageView.setImageBitmap((Bitmap) o);
                                refreshPassCode = false;
                            }
                            else {
                                randCodeImg = (Bitmap) o;
                                buildPassCodeDialog();
                            }
                        }
                        else if (o instanceof LoopResponse) {
                            LoopResponse response = (LoopResponse) o;
                            trainCode.setText(response.getTrainCode());
                            rwNum.setText(response.getRwNum());
                            ywNum.setText(response.getYwNum());
                            yzNum.setText(response.getYzNum());
                            wzNum.setText(response.getWzNum());
                        }
                        else if (o.toString().equals("get")) {
                            stopService(new Intent(MainActivity.this, LoopService.class));
                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            Notification notification = new Notification.Builder(MainActivity.this)
                                    .setContentTitle("Tickets")
                                    .setContentText("打开12306看看")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .build();
                            manager.notify(1, notification);
                        }
                        else {
                            if (!o.toString().contains(",")) {
                                showLongToast(o.toString());
                            }
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
        SubscriptionUtil.unSubscribe(progressbarBus);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerBus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
