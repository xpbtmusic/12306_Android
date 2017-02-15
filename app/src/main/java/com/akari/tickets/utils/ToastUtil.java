package com.akari.tickets.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Akari on 2017/2/15.
 */

public class ToastUtil {
    private static Toast toast;

    public static void showShortToast(Context context, CharSequence text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showLongToast(Context context, CharSequence text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        }
        else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }
}
