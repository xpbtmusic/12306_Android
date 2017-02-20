package com.akari.tickets.utils;

import com.akari.tickets.beans.OrderParam;
import com.akari.tickets.beans.Passenger;
import com.akari.tickets.beans.QueryParam;

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
    public static String seat_type_codes;

    public static void getOrderParam(JSONObject object, OrderParam orderParam) {
        try {
            StringBuilder passengerTicketStr = new StringBuilder();
            passengerTicketStr.append(seat_type_codes);
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
