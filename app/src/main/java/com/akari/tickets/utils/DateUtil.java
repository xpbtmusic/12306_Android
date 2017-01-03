package com.akari.tickets.utils;

/**
 * Created by Akari on 2017/1/3.
 */

public class DateUtil {
    public static String getDateStr(int year, int month, int day) {
        String monthStr = (month + 1) + "";
        String dayStr = day + "";
        if (month + 1 < 10) {
            monthStr = "0" + (month + 1);
        }
        if (day < 10) {
            dayStr = "0" + day;
        }
        return year + "-" + monthStr + "-" + dayStr;
    }
}
