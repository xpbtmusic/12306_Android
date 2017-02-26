package com.akari.tickets.utils;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.akari.tickets.ui.adapter.BaseAdapter;

/**
 * Created by Akari on 2017/2/25.
 */

public class AlertDialogUtil {
    public static AlertDialog.Builder setButton(AlertDialog.Builder builder, final BaseAdapter adapter, final TextView view) {
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for (int i = 0; i < adapter.getList().size(); i ++) {
                    if (adapter.getCheckStatusList().get(i)) {
                        if (first) {
                            builder.append(adapter.getList().get(i));
                            first = false;
                        }
                        else {
                            builder.append(", ");
                            builder.append(adapter.getList().get(i));
                        }
                    }
                }
                view.setText(builder.toString());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }
}
