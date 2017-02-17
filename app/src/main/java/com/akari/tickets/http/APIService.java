package com.akari.tickets.http;

import com.akari.tickets.beans.CheckRandCodeResponse;
import com.akari.tickets.beans.LoginSuggestResponse;
import com.akari.tickets.beans.QueryTrainsResponse;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Akari on 2017/2/14.
 */

public interface APIService {
    @GET("login/init")
    Observable<ResponseBody> loginInit();

    @GET("passcodeNew/getPassCodeNew")
    Observable<ResponseBody> getPassCode(@Query("module") String module, @Query("rand") String rand);

    @FormUrlEncoded
    @POST("passcodeNew/checkRandCodeAnsyn")
    Observable<CheckRandCodeResponse> checkRandCode(@Field(value = "randCode", encoded = true) String randCode, @Field("rand") String rand);

    @FormUrlEncoded
    @POST("login/loginAysnSuggest")
    Observable<LoginSuggestResponse> loginSuggest(@Field("loginUserDTO.user_name") String username, @Field("userDTO.password") String password,
                                                  @Field(value = "randCode", encoded = true) String randCode);

    @FormUrlEncoded
    @POST("login/userLogin")
    Observable<ResponseBody> userLogin(@Field("_json_att") String param);

    @FormUrlEncoded
    @POST("passengers/init")
    Observable<ResponseBody> initPassengers(@Field("_json_att") String param);

    @GET("leftTicket/queryX")
    Observable<ResponseBody> queryTrains(@Query("leftTicketDTO.train_date") String trainDate, @Query("leftTicketDTO.from_station") String fromStation,
                                                @Query("leftTicketDTO.to_station") String toStation, @Query("purpose_codes") String purposeCode);
}
