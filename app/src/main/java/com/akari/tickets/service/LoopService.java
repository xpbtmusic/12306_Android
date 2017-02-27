package com.akari.tickets.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import com.akari.tickets.beans.CheckOrderInfoResponse;
import com.akari.tickets.beans.CheckRandCodeResponse;
import com.akari.tickets.beans.ConfirmSingleForQueueResponse;
import com.akari.tickets.beans.GetQueueCountResponse;
import com.akari.tickets.beans.LoopResponse;
import com.akari.tickets.beans.OrderParam;
import com.akari.tickets.beans.QueryLeftNewDTO;
import com.akari.tickets.beans.QueryOrderWaitTimeResponse;
import com.akari.tickets.beans.QueryParam;
import com.akari.tickets.beans.QueryTrainsResponse;
import com.akari.tickets.beans.ResultOrderResponse;
import com.akari.tickets.beans.SubmitOrderResponse;
import com.akari.tickets.network.HttpService;
import com.akari.tickets.network.RetrofitManager;
import com.akari.tickets.rxbus.RxBus;
import com.akari.tickets.utils.OrderUtil;
import com.akari.tickets.utils.SubscriptionUtil;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
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

public class LoopService extends Service {
    private static boolean breakChooseSeats = false;
    private static QueryParam queryParam;
    private static OrderParam orderParam;
    private static Map<String, String> map;
    private static Bitmap randCodeImg;
    private static String randCode = "";
    private static boolean showPassCode = false;
    private String leftTicketUrl = "leftTicket/queryA";
    private static String trainDate;
    private static QueryLeftNewDTO queryLeftNewDTO;

    private Subscription queryTrainLoopSubscription;
    private Subscription orderSubscription;
    private Subscription queryWaitTimeLoopSubscription;
    private Subscription orderCompleteSubscription;

    public LoopService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        leftTicketUrl = intent.getStringExtra("leftTicketUrl");
        queryParam = intent.getParcelableExtra("queryParam");

        RxBus.getDefault().toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o.toString().contains(",")) {
                            randCode = o.toString();
                            checkRandCodeAndNext();
                        }
                    }
                });

        startQueryLoop();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startQueryLoop() {
        breakChooseSeats = false;
        RxBus.getDefault().post("start");
        SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
        final HttpService service = RetrofitManager.getInstance().getService();
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
                        RxBus.getDefault().post("start");
                        trainDate = queryParam.getTrain_date();
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
                        RxBus.getDefault().post("count");
                        for (QueryTrainsResponse.Data data : queryTrainsResponse.getData()) {
                            if (data.getQueryLeftNewDTO().getStation_train_code().equals(queryParam.getTrain_code())) {
                                LoopResponse response = new LoopResponse();
                                response.setTrainCode(queryParam.getTrain_code());
                                response.setRwNum(data.getQueryLeftNewDTO().getRw_num());
                                response.setYwNum(data.getQueryLeftNewDTO().getYw_num());
                                response.setYzNum(data.getQueryLeftNewDTO().getYz_num());
                                response.setWzNum(data.getQueryLeftNewDTO().getWz_num());
                                RxBus.getDefault().post(response);
                            }
                            if (!data.getSecretStr().equals("")) {
                                if (data.getQueryLeftNewDTO().getStation_train_code().equals(queryParam.getTrain_code())) {
                                    queryLeftNewDTO = data.getQueryLeftNewDTO();
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
                    RxBus.getDefault().post("button");
                    OrderUtil.seat_type_codes = "4";
                    breakChooseSeats = true;
                    RxBus.getDefault().post("stop");
                    submitOrder(secretStr, queryParam);
                }
                break;
            case "硬卧":
                if (!queryLeftNewDTO.getYw_num().equals("无") && !queryLeftNewDTO.getYw_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
                    RxBus.getDefault().post("button");
                    OrderUtil.seat_type_codes = "3";
                    breakChooseSeats = true;
                    RxBus.getDefault().post("stop");
                    submitOrder(secretStr, queryParam);
                }
                break;
            case "硬座":
                if (!queryLeftNewDTO.getYz_num().equals("无") && !queryLeftNewDTO.getYz_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
                    RxBus.getDefault().post("button");
                    OrderUtil.seat_type_codes = "1";
                    breakChooseSeats = true;
                    RxBus.getDefault().post("stop");
                    submitOrder(secretStr, queryParam);
                }
                break;
            case "无座":
                if (!queryLeftNewDTO.getWz_num().equals("无") && !queryLeftNewDTO.getWz_num().equals("--")) {
                    SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
                    RxBus.getDefault().post("button");
                    OrderUtil.seat_type_codes = "1";
                    breakChooseSeats = true;
                    RxBus.getDefault().post("stop");
                    submitOrder(secretStr, queryParam);
                }
                break;
            default:
                break;
        }
    }

    private void submitOrder(String secretStr, final QueryParam queryParam) {
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
                .flatMap(new Func1<SubmitOrderResponse, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(SubmitOrderResponse submitOrderResponse) {
                        if (!submitOrderResponse.isStatus()) {
                            RxBus.getDefault().post(submitOrderResponse.getMessages()[0]);
                        }
                        else {
                            return service.initDc("");
                        }
                        return null;
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
                .flatMap(new Func1<CheckOrderInfoResponse, Observable<GetQueueCountResponse>>() {
                    @Override
                    public Observable<GetQueueCountResponse> call(CheckOrderInfoResponse checkOrderInfoResponse) {
                        CheckOrderInfoResponse.Data data = checkOrderInfoResponse.getData();
                        if (data.isSubmitStatus()) {
                            if (data.getIfShowPassCode().equals("Y")) {
                                showPassCode = true;
                            }
                            map.clear();
                            map.put("train_date", getTrainDate());
                            map.put("train_no", queryLeftNewDTO.getTrain_no());
                            map.put("stationTrainCode", queryParam.getTrain_code());
                            map.put("seatType", OrderUtil.seat_type_codes);
                            map.put("fromStationTelecode", queryLeftNewDTO.getFrom_station_telecode());
                            map.put("toStationTelecode", queryLeftNewDTO.getTo_station_telecode());
                            map.put("leftTicket", orderParam.getLeftTicketStr());
                            map.put("purpose_codes", orderParam.getPurpose_codes());
                            map.put("train_location", queryLeftNewDTO.getLocation_code());
                            map.put("_json_att", "");
                            map.put("REPEAT_SUBMIT_TOKEN", orderParam.getREPEAT_SUBMIT_TOKEN());

                            return service.getQueueCount(map);
                        }
                        else {
                            RxBus.getDefault().post(data.getErrMsg());
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GetQueueCountResponse>() {
                    @Override
                    public void call(GetQueueCountResponse getQueueCountResponse) {
                        GetQueueCountResponse.Data data = getQueueCountResponse.getData();
                        String ticketCount;
                        if (data.getTicket().contains(",")) {
                            ticketCount = data.getTicket().split(",")[0];
                        }
                        else {
                            ticketCount = data.getTicket();
                        }
                        if (Integer.parseInt(ticketCount) > 0) {
                            if (showPassCode) {
                                showPassCode = false;
                                RxBus.getDefault().post(randCodeImg);
                            }
                            else {
                                submitOrderNext(service, orderParam.getREPEAT_SUBMIT_TOKEN());
                            }
                        }
                        else {
                            startQueryLoop();
                        }
                    }
                });
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
                            RxBus.getDefault().post("pass");
                            submitOrderNext(service, orderParam.getREPEAT_SUBMIT_TOKEN());
                        }
                        else {
                            RxBus.getDefault().post("验证码错误");
                            RxBus.getDefault().post("clear");
                            refreshPassCode();
                        }
                    }
                });
    }

    private void refreshPassCode() {
        HttpService service = RetrofitManager.getInstance().getService();
        service.getPassCode("passenger", "randp")
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
                        RxBus.getDefault().post("refreshPassCode");
                        RxBus.getDefault().post(bitmap);
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
                        RxBus.getDefault().post("get");
                    }
                });
    }

    private String getRandom() {
        String s = new Random().nextDouble() + "";
        return s.split("\\.")[1].substring(0, 13);
    }

    private String getTrainDate() {
        int year = Integer.parseInt(trainDate.split("-")[0]);
        int month = Integer.parseInt(trainDate.split("-")[1]) - 1;
        int day = Integer.parseInt(trainDate.split("-")[2]);
        Date date = new Date(year - 1900, month, day);
        String[] ss = date.toString().split(" ");
        return ss[0] + " " + ss[1] + " " + ss[2] + " " + ss[5] + " 00:00:00 GMT+0800 (中国标准时间)";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SubscriptionUtil.unSubscribe(queryTrainLoopSubscription);
        SubscriptionUtil.unSubscribe(orderSubscription);
        SubscriptionUtil.unSubscribe(queryWaitTimeLoopSubscription);
        SubscriptionUtil.unSubscribe(orderCompleteSubscription);
    }
}
