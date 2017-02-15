package com.akari.tickets.retrofit;

import com.akari.tickets.retrofit.interceptor.AddCookieInterceptor;
import com.akari.tickets.retrofit.interceptor.ReceiveCookieInterceptor;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Akari on 2017/2/14.
 */

public class RetrofitManager {
    private static RetrofitManager manager;
    private Retrofit retrofit;
    private static final int TIME_OUT = 10000;
    public static String cookie = "";

    private RetrofitManager() {

    }

    public static RetrofitManager getInstance() {
        if (manager == null) {
            manager = new RetrofitManager();
        }
        return manager;
    }

    private OkHttpClient getClient() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new ReceiveCookieInterceptor())
                .addInterceptor(new AddCookieInterceptor())
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        return client;
    }

    public TicketsService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://kyfw.12306.cn/otn/")
                    .client(getClient())
                    .build();
        }
        return retrofit.create(TicketsService.class);
    }

    public TicketsService getService(Converter.Factory factory) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://kyfw.12306.cn/otn/")
                .client(getClient())
                .addConverterFactory(factory)
                .build();
        return retrofit.create(TicketsService.class);
    }
}
