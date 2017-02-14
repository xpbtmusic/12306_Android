package com.akari.tickets.retrofit;

import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Akari on 2017/2/14.
 */

public class RetrofitManager {
    private static RetrofitManager manager;
    private Retrofit retrofit;

    private RetrofitManager() {

    }

    public static RetrofitManager getInstance() {
        if (manager == null) {
            manager = new RetrofitManager();
        }
        return manager;
    }

    public TicketsService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://kyfw.12306.cn/otn/")
                    .build();
        }
        return retrofit.create(TicketsService.class);
    }

    public TicketsService getService(Converter.Factory factory) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://kyfw.12306.cn/otn/")
                .addConverterFactory(factory)
                .build();
        return retrofit.create(TicketsService.class);
    }
}
