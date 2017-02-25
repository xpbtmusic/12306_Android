package com.akari.tickets.network.interceptor;

import com.akari.tickets.network.RetrofitManager;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Akari on 2017/2/15.
 */

public class ReceiveCookieInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            List<String> setCookies = originalResponse.headers("Set-Cookie");
            StringBuilder cookieBuilder = new StringBuilder();
            for (String setCookie : setCookies) {
                cookieBuilder.append(setCookie);
                cookieBuilder.append(";");
            }

            String cookie = cookieBuilder.toString()
                    .replaceAll(" path=/;", "")
                    .replaceAll(" Path=/;", "")
                    .replaceAll(" Path=/otn;", "");
            if (!RetrofitManager.cookie.contains(cookie)) {
                RetrofitManager.cookie = RetrofitManager.cookie + cookie;
            }
        }
        return originalResponse;
    }
}
