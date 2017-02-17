package com.akari.tickets.retrofit;

import com.akari.tickets.retrofit.interceptor.AddCookieInterceptor;
import com.akari.tickets.retrofit.interceptor.ReceiveCookieInterceptor;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
        return new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new ReceiveCookieInterceptor())
                .addInterceptor(new AddCookieInterceptor())
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
    }

    public APIService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://kyfw.12306.cn/otn/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())
                    .build();
        }
        return retrofit.create(APIService.class);
    }
}
