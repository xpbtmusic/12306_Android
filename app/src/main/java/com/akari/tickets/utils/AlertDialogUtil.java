package com.akari.tickets.utils;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Akari on 2017/2/25.
 */

public class AlertDialogUtil {
    public static AlertDialog.Builder setButton(AlertDialog.Builder builder, AlertDialog.OnClickListener listener) {
        builder.setPositiveButton("确定", listener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }
}
