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

    public static void startQueryLoop(QueryParam queryParam) {
        QueryUtil.queryParam = queryParam;
        HttpUtil.get(queryParam.getUrl(), new LoopCallBack());
    }

    private static class LoopCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            System.out.println(json);
            JSONObject object;
            JSONObject object1;
            String[] seats;
            try {
                JSONArray array = new JSONObject(json).getJSONArray("data");
                if (array.length() != 0) {
                    for (int i = 0; i < array.length(); i++) {
                        object = array.getJSONObject(i);
                        if (!object.getString("secretStr").equals("")) {
                            object1 = object.getJSONObject("queryLeftNewDTO");
                            if (object1.getString("station_train_code").equals(queryParam.getTrain_code())) {
                                seats = queryParam.getSeats();
                                for (String seat : seats) {
                                    querySeats(object1, seat);
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void querySeats(JSONObject object, String seat) throws JSONException {
        switch (seat) {
            case "软卧":
                if (!object.getString("rw_num").equals("无") && !object.getString("rw_num").equals("--")) {
                    System.out.println("软卧 GET!!!");
                }
                break;
            case "硬卧":
                if (!object.getString("yw_num").equals("无") && !object.getString("rw_num").equals("--")) {
                    System.out.println("硬卧 GET!!!");
                }
                break;
            case "硬座":
                if (!object.getString("yz_num").equals("无") && !object.getString("rw_num").equals("--")) {
                    System.out.println("硬座 GET!!!");
                }
                break;
            case "无座":
                if (!object.getString("wz_num").equals("无") && !object.getString("rw_num").equals("--")) {
                    System.out.println("无座 GET!!!");
                }
                break;
            default:
                break;
        }
    }
}
