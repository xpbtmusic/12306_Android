package com.akari.tickets.utils;

import com.akari.tickets.beans.OrderParam;
import com.akari.tickets.beans.Passenger;
import com.akari.tickets.beans.QueryParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by Akari on 2017/1/5.
 */

public class OrderUtil {

    private static OrderParam orderParam;

    public static void submitOrder(String secretStr, QueryParam queryParam) {
        if (!HttpUtil.cookie.contains("_jc_save_fromStation")) {
            HttpUtil.cookie = HttpUtil.cookie + ";" + "_jc_save_fromStation=" + encodeStr(queryParam.getFrom_station()) + "%2C" + queryParam.getFrom_station_code()
                    + ";" + "_jc_save_toStation=" + encodeStr(queryParam.getTo_station()) + "%2C" + queryParam.getTo_station_code() + ";" + "_jc_save_fromDate="
                    + queryParam.getTrain_date() + ";" + "_jc_save_toDate=" + queryParam.getBack_train_date() + "; _jc_save_wfdc_flag=dc";
        }

        String url = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("secretStr", secretStr.replaceAll("%2F", "/").replaceAll("%2B", "+").replaceAll("%0A", "\n"));
        builder.add("train_date", queryParam.getTrain_date());
        builder.add("back_train_date", queryParam.getBack_train_date());
        builder.add("tour_flag", "dc");
        builder.add("purpose_codes", queryParam.getPurpose_codes());
        builder.add("query_from_station_name", queryParam.getFrom_station());
        builder.add("query_to_station_name", queryParam.getTo_station());
        builder.add("undefined", "");

        HttpUtil.post(url, builder.build(), new SubmitOrderCallBack());
    }

    private static String encodeStr(String s) {
        StringBuilder builder = new StringBuilder();
        try {
            String unicodeStr = URLEncoder.encode(s, "Unicode");
            String[] strs = unicodeStr.split("%");
            for (int i = 3; i < strs.length - 1; i = i + 2) {
                builder.append("%u");
                builder.append(strs[i + 1]);
                builder.append(strs[i]);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static class SubmitOrderCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            System.out.println(json);
            try {
                JSONObject object = new JSONObject(json);
                if (object.getBoolean("status")) {
                    String url = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("_json_att", "");

                    HttpUtil.post(url, builder.build(), new InitDcCallBack());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static class InitDcCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String s = response.body().string();
            String globalRepeatSubmitToken = s.split("globalRepeatSubmitToken = '")[1].split("';")[0];
            orderParam = new OrderParam();
            orderParam.setREPEAT_SUBMIT_TOKEN(globalRepeatSubmitToken);

            try {
                JSONObject object = new JSONObject(s.split("ticketInfoForPassengerForm=")[1].split(";")[0]);
                System.out.println("OK!");
                getOrderParam(object, orderParam);

                String url = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("cancel_flag", "2");
                builder.add("bed_level_order_num", "000000000000000000000000000000");
                builder.add("passengerTicketStr", orderParam.getPassengerTicketStr());
                builder.add("oldPassengerStr", orderParam.getOldPassengerStr());
                builder.add("tour_flag", "dc");
                builder.add("randCode", "");
                builder.add("_json_att", "");
                builder.add("REPEAT_SUBMIT_TOKEN", orderParam.getREPEAT_SUBMIT_TOKEN());
                HttpUtil.post(url, builder.build(), new CheckOrderInfoCallBack());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static class CheckOrderInfoCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            try {
                JSONObject object = new JSONObject(json);
                JSONObject data = object.getJSONObject("data");
                if (data.getBoolean("submitStatus")) {
                    String url = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("passengerTicketStr", orderParam.getPassengerTicketStr());
                    builder.add("oldPassengerStr", orderParam.getOldPassengerStr());
                    builder.add("randCode", orderParam.getRandCode());
                    builder.add("purpose_codes", orderParam.getPurpose_codes());
                    builder.add("key_check_isChange", orderParam.getKey_check_isChange());
                    builder.add("leftTicketStr", orderParam.getLeftTicketStr());
                    builder.add("train_location", orderParam.getTrain_location());
                    builder.add("choose_seats", orderParam.getChoose_seats());
                    builder.add("seatDetailType", orderParam.getSeatDetailType());
                    builder.add("roomType", orderParam.getRoomType());
                    builder.add("dwAll", orderParam.getDwAll());
                    builder.add("_json_att", orderParam.get_json_att());
                    builder.add("REPEAT_SUBMIT_TOKEN", orderParam.getREPEAT_SUBMIT_TOKEN());

                    HttpUtil.post(url, builder.build(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String json = response.body().string();
                            System.out.println(json);
                            try {
                                JSONObject data = new JSONObject(json).getJSONObject("data");
                                if (data.getBoolean("submitStatus")) {
                                    System.out.println("打开12306看看");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void getOrderParam(JSONObject object, OrderParam orderParam) {
        try {
            StringBuilder passengerTicketStr = new StringBuilder();
            JSONObject limitBuySeatTicketDTO = object.getJSONObject("limitBuySeatTicketDTO");
            JSONArray seat_type_codes = limitBuySeatTicketDTO.getJSONArray("seat_type_codes");
            passengerTicketStr.append(seat_type_codes.getJSONObject(seat_type_codes.length() - 1).getString("id"));
            passengerTicketStr.append(",0,1,");

            Passenger passenger = PassengerUtil.getPassenger(PassengerUtil.selectedPassenger);
            passengerTicketStr.append(passenger.getPassenger_name());
            passengerTicketStr.append(",1,");
            passengerTicketStr.append(passenger.getIdCard());
            passengerTicketStr.append(",");
            passengerTicketStr.append(passenger.getPhoneNum());
            passengerTicketStr.append(",N");
            orderParam.setPassengerTicketStr(passengerTicketStr.toString());

            StringBuilder oldPassengerStr = new StringBuilder();
            oldPassengerStr.append(passenger.getPassenger_name());
            oldPassengerStr.append(",1,");
            oldPassengerStr.append(passenger.getIdCard());
            oldPassengerStr.append(",1_");
            orderParam.setOldPassengerStr(oldPassengerStr.toString());

            orderParam.setRandCode("");
            orderParam.setPurpose_codes(object.getString("purpose_codes"));
            orderParam.setKey_check_isChange(object.getString("key_check_isChange"));
            orderParam.setLeftTicketStr(object.getString("leftTicketStr"));
            orderParam.setTrain_location(object.getString("train_location"));
            orderParam.setChoose_seats("");
            orderParam.setSeatDetailType("000");
            orderParam.setRoomType("00");
            orderParam.setDwAll("N");
            orderParam.set_json_att("");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
