package com.akari.tickets.network;

import com.akari.tickets.beans.CheckOrderInfoResponse;
import com.akari.tickets.beans.CheckRandCodeResponse;
import com.akari.tickets.beans.ConfirmSingleForQueueResponse;
import com.akari.tickets.beans.LoginSuggestResponse;
import com.akari.tickets.beans.QueryTrainsResponse;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Akari on 2017/2/14.
 */

public interface HttpService {
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

    @GET("leftTicket/init")
    Observable<ResponseBody> getLeftTicketUrl();

    @GET("{leftTicketUrl}")
    Observable<QueryTrainsResponse> queryTrains(@Path(value = "leftTicketUrl", encoded = true) String leftTicketUrl, @Query("leftTicketDTO.train_date") String trainDate, @Query("leftTicketDTO.from_station") String fromStation,
                                                @Query("leftTicketDTO.to_station") String toStation, @Query("purpose_codes") String purposeCode);

    @FormUrlEncoded
    @POST("leftTicket/submitOrderRequest")
    Observable<ResponseBody> submitOrder(@FieldMap(encoded = true) Map<String, String> fields);

    @FormUrlEncoded
    @POST("confirmPassenger/initDc")
    Observable<ResponseBody> initDc(@Field("_json_att") String _json_att);

    @FormUrlEncoded
    @POST("confirmPassenger/checkOrderInfo")
    Observable<CheckOrderInfoResponse> checkOrderInfo(@FieldMap(encoded = true) Map<String, String> fields);

    @FormUrlEncoded
    @POST("confirmPassenger/confirmSingleForQueue")
    Observable<ConfirmSingleForQueueResponse> confirmSingleForQueue(@FieldMap Map<String, String> field);
}
