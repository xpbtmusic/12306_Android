package com.akari.tickets.utils;

import android.content.Context;

import com.akari.tickets.R;
import java.util.HashMap;

/**
 * Created by Akari on 2016/12/31.
 */

public class StationCodeUtil {

    private static HashMap<String, String> name2CodeMap;

    public static void init(Context context) {
        String s1 = context.getResources().getString(R.string.s1);
        String s2 = context.getResources().getString(R.string.s2);

        String[] stations1 = s1.split("@");
        String[] stations2 = s2.split("@");
        String[] stations = new String[stations1.length + stations2.length];
        System.arraycopy(stations1, 0, stations, 0, stations1.length);
        System.arraycopy(stations2, 0, stations, stations1.length, stations2.length);

        name2CodeMap = new HashMap<>();
        for (String s : stations) {
            name2CodeMap.put(s.split("\\|")[1], s.split("\\|")[2]);
        }
    }

    public static HashMap<String, String> getName2CodeMap() {
        return name2CodeMap;
    }
}
