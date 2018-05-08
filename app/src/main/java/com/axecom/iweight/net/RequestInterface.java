package com.axecom.iweight.net;


import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017-11-27.
 */

public interface RequestInterface {
//    @GET("http://122.226.100.91/adyen/listRecurringDetails/uin/102802")
//    Observable<AdyenUserInfoBean> getUserInfo();
//
//    @HTTP(method = "GET", path = "adyen/listRecurringDetails/uin/{uId}", hasBody = false)
//    Call<AdyenUserInfoBean> getUserInfoByHttp(@Path("uId") int uId);
//
//    @POST("recurringPay")
//    Call<PayResultBaen> recurringPay(@Body PayBean payBean);
//
//    @POST("recurringPayByRxJava")
//    Observable<BaseEntity<PayResultBaen>> recurringPayByRxJava(@Body PayBean bean);
//
//    @GET("mobile/get")
//    Observable<PhoneBean<Result>> getPhoneAddress(@Query("phone") long phone, @Query("key") String key);

    @GET("test.php")
    Observable<String> test();
}
