package com.akari.tickets.network.interceptor;

import com.akari.tickets.network.RetrofitManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Akari on 2017/2/15.
 */

public class AddCookieInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Cookie", RetrofitManager.cookie);
        return chain.proceed(builder.build());
    }
}
