package com.akari.tickets.utils;

import android.view.View;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Akari on 2017/2/25.
 */

public class RandCodeUtil {
    public static void toggle(View view) {
        if (view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static String getRandCode(List<ImageView> list) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getVisibility() == View.VISIBLE) {
                if (first) {
                    first = false;
                }
                else {
                    builder.append(",");
                }
                if (i < 4) {
                    builder.append(35 + i * 70);
                    builder.append(",");
                    builder.append("35");
                }
                else {
                    builder.append(35 + (i - 4) * 70);
                    builder.append(",");
                    builder.append("105");
                }
            }
        }
        return builder.toString();
    }

    public static void clearSelected(List<ImageView> list) {
        for (ImageView view : list) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void changeSelectedStatus(float x, float y, List<ImageView> list, boolean login) {
        if (login) {
            if (y > 10 && y < 77) {
                if (x > 5 && x < 72) {
                    RandCodeUtil.toggle(list.get(0));
                }
                else if (x > 77 && x < 144) {
                    RandCodeUtil.toggle(list.get(1));
                }
                else if (x > 149 && x < 216) {
                    RandCodeUtil.toggle(list.get(2));
                }
                else if (x > 221 && x < 288) {
                    RandCodeUtil.toggle(list.get(3));
                }
            }
            else if (y > 82 && y < 149) {
                if (x > 5 && x < 72) {
                    RandCodeUtil.toggle(list.get(4));
                }
                else if (x > 77 && x < 144) {
                    RandCodeUtil.toggle(list.get(5));
                }
                else if (x > 149 && x < 216) {
                    RandCodeUtil.toggle(list.get(6));
                }
                else if (x > 221 && x < 288) {
                    RandCodeUtil.toggle(list.get(7));
                }
            }
        }
        else {
            if (y > 10 && y < 73) {
                if (x > 5 && x < 67) {
                    RandCodeUtil.toggle(list.get(0));
                }
                else if (x > 72 && x < 135) {
                    RandCodeUtil.toggle(list.get(1));
                }
                else if (x > 140 && x < 203) {
                    RandCodeUtil.toggle(list.get(2));
                }
                else if (x > 208 && x < 271) {
                    RandCodeUtil.toggle(list.get(3));
                }
            }
            else if (y > 78 && y < 141) {
                if (x > 5 && x < 67) {
                    RandCodeUtil.toggle(list.get(4));
                }
                else if (x > 72 && x < 135) {
                    RandCodeUtil.toggle(list.get(5));
                }
                else if (x > 140 && x < 203) {
                    RandCodeUtil.toggle(list.get(6));
                }
                else if (x > 208 && x < 271) {
                    RandCodeUtil.toggle(list.get(7));
                }
            }
        }
    }
}
