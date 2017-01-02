package com.akari.tickets.utils;

import com.akari.tickets.beans.Passenger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akari on 2017/1/2.
 */

public class PassengerUtil {

    private static List<Passenger> list;

    public static void savePassengers(String json) {
        list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            JSONObject object;
            Passenger passenger;
            for (int i = 0; i < array.length(); i++) {
                object = array.getJSONObject(i);
                passenger = new Passenger();
                passenger.setPassenger_name(object.getString("passenger_name"));
                passenger.setPassenger_type_name(object.getString("passenger_type_name"));
                passenger.setIsUserSelf(object.getString("isUserSelf"));
                list.add(passenger);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<Passenger> getPassengers() {
        return list;
    }

    public static Passenger getUserSelf() {
        for (Passenger p : list) {
            if (p.getIsUserSelf().equals("Y")) {
                return p;
            }
        }
        return null;
    }
}
