package com.akari.tickets.retrofit;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Akari on 2017/2/14.
 */

public interface TicketsService {
    @GET("login/init")
    Observable<ResponseBody> loginInit();

    @GET("passcodeNew/getPassCodeNew")
    Observable<ResponseBody> getPassCode(@Query("module") String module, @Query("rand") String rand);

    @FormUrlEncoded
    @POST("passcodeNew/checkRandCodeAnsyn")
    Call<ResponseBody> checkRandCode(@Field(value = "randCode", encoded = true) String randCode, @Field("rand") String rand);

    @FormUrlEncoded
    @POST("login/loginAysnSuggest")
    Call<ResponseBody> loginSuggest(@Field("loginUserDTO.user_name") String username, @Field("userDTO.password") String password, @Field(value = "randCode", encoded = true) String randCode);

    @FormUrlEncoded
    @POST("login/userLogin")
    Call<ResponseBody> userLogin(@Field("_json_att") String param);

    @FormUrlEncoded
    @POST("passengers/init")
    Call<ResponseBody> getPassengers(@Field("_json_att") String param);


}
