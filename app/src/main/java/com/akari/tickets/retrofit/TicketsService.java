package com.akari.tickets.retrofit;

import com.akari.tickets.beans.CheckRandCodeResponse;
import com.akari.tickets.beans.CookieResponse;
import com.akari.tickets.beans.LoginSuggestResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Akari on 2017/2/14.
 */

public interface TicketsService {
    @GET("login/init")
    Call<CookieResponse> getCookie();

    @GET("passcodeNew/getPassCodeNew")
    Call<ResponseBody> passCode(@Query("module") String module, @Query("rand") String rand);

    @FormUrlEncoded
    @POST("passcodeNew/checkRandCodeAnsyn")
    Call<CheckRandCodeResponse> checkRandCode(@Field("randCode") String randCode, @Field("rand") String rand);

    @FormUrlEncoded
    @POST("login/loginAysnSuggest")
    Call<LoginSuggestResponse> loginSuggest(@Field("loginUserDTO.user_name") String username, @Field("userDTO.password") String password, @Field("randCode") String randCode);

    @FormUrlEncoded
    @POST("login/userLogin")
    Call<ResponseBody> userLogin(@Field("_json_att") String param);

    @FormUrlEncoded
    @POST("passengers/init")
    Call<ResponseBody> getPassengers(@Field("_json_att") String param);


}
