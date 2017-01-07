package com.akari.tickets.utils;


import com.akari.tickets.beans.QueryParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by Akari on 2017/1/3.
 */

public class QueryUtil {
    private static QueryParam queryParam;
    public static boolean get = false;
    public static MyThread thread;
    private static int n = 1;
    public static String log = "";
    public static boolean end = false;

    public static void startQueryLoop(QueryParam queryParam) {
        if (QueryUtil.queryParam == null) {
            QueryUtil.queryParam = queryParam;
        }

        HttpUtil.get(queryParam.getUrl(), new LoopCallBack());

        if (thread == null) {
            thread = new MyThread();
            thread.start();
        }

    }

    private static class LoopCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            System.out.println("正在查询..." + queryParam.getFrom_station() + " - " + queryParam.getTo_station() + "..." + n++);
            log = "正在查询..." + queryParam.getFrom_station() + " - " + queryParam.getTo_station() + "..." + n + "\n";
            JSONObject object;
            JSONObject object1;
            String[] seats;
            String secretStr;
            try {
                JSONArray array = new JSONObject(json).getJSONArray("data");
                if (array.length() != 0) {
                    for (int i = 0; i < array.length(); i++) {
                        object = array.getJSONObject(i);
                        if (!object.getString("secretStr").equals("")) {
                            secretStr = object.getString("secretStr");
                            object1 = object.getJSONObject("queryLeftNewDTO");
                            if (object1.getString("station_train_code").equals(queryParam.getTrain_code())) {
                                seats = queryParam.getSeats();
                                for (String seat : seats) {
                                    querySeats(object1, seat, secretStr);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized void querySeats(JSONObject object, String seat, String secretStr) throws JSONException, InterruptedException {
        switch (seat) {
            case "软卧":
                if (!object.getString("rw_num").equals("无") && !object.getString("rw_num").equals("--")) {
                    get = true;
                    thread.interrupt();
                    System.out.println("正在抢软卧...");
                    QueryUtil.log = "正在抢软卧...";
                    OrderUtil.seat_type_codes = "4";
                    OrderUtil.submitOrder(secretStr, queryParam);
                }
                break;
            case "硬卧":
                if (!object.getString("yw_num").equals("无") && !object.getString("yw_num").equals("--")) {
                    get = true;
                    thread.interrupt();
                    System.out.println("正在抢硬卧...");
                    QueryUtil.log = "正在抢硬卧...";
                    OrderUtil.seat_type_codes = "3";
                    OrderUtil.submitOrder(secretStr, queryParam);
                }
                break;
            case "硬座":
                if (!object.getString("yz_num").equals("无") && !object.getString("yz_num").equals("--")) {
                    get = true;
                    thread.interrupt();
                    System.out.println("正在抢硬座...");
                    QueryUtil.log = "正在抢硬座...";
                    OrderUtil.seat_type_codes = "1";
                    OrderUtil.submitOrder(secretStr, queryParam);
                }
                break;
            case "无座":
                if (!object.getString("wz_num").equals("无") && !object.getString("wz_num").equals("--")) {
                    get = true;
                    thread.interrupt();
                    System.out.println("正在抢无座...");
                    QueryUtil.log = "正在抢无座...";
                    OrderUtil.seat_type_codes = "1";
                    OrderUtil.submitOrder(secretStr, queryParam);
                }
                break;
            default:
                break;
        }
    }

    private static class MyThread extends Thread {
        @Override
        public void run() {
            while (!get) {
                String url;
                if (!queryParam.getDate2()[0].equals("")) {
                    for (int i = -1; i < queryParam.getDate2().length; i++) {
                        if (get) {
                            break;
                        }
                        if (i == -1) {
                            url = "https://kyfw.12306.cn/otn/leftTicket/queryA?leftTicketDTO.train_date=" + queryParam.getTrain_date() + "&leftTicketDTO.from_station=" + queryParam.getFrom_station_code()
                                    + "&leftTicketDTO.to_station=" + queryParam.getTo_station_code() + "&purpose_codes=" + queryParam.getPurpose_codes();
                        }
                        else {
                            url = "https://kyfw.12306.cn/otn/leftTicket/queryA?leftTicketDTO.train_date=" + queryParam.getDate2()[i] + "&leftTicketDTO.from_station=" + queryParam.getFrom_station_code()
                                    + "&leftTicketDTO.to_station=" + queryParam.getTo_station_code() + "&purpose_codes=" + queryParam.getPurpose_codes();
                        }
                        HttpUtil.get(url, new LoopCallBack());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    HttpUtil.get(queryParam.getUrl(), new LoopCallBack());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
