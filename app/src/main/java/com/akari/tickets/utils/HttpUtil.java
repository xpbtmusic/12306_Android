package com.akari.tickets.utils;

import com.zhy.http.okhttp.https.HttpsUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Akari on 2017/1/2.
 */

public class HttpUtil {

    private static OkHttpClient client;
    private static boolean init = false;
    public static String cookie = "";

    private static void init() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        client = new OkHttpClient.Builder().connectTimeout(3000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        init = true;
    }

    public static void get(String url, Callback callback) {
        if (!init) {
            init();
        }
        Request request = new Request.Builder().url(url)
                .addHeader("Cookie", cookie)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void post(String url, RequestBody body, Callback callback) {
        if (!init) {
            init();
        }
        Request request = new Request.Builder().url(url)
                .addHeader("Cookie", cookie)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
