package com.axecom.iweight.net;


import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.CalibrationBean;
import com.axecom.iweight.bean.LocalSettingsBean;
import com.axecom.iweight.bean.LoginData;
import com.axecom.iweight.bean.LoginInfo;
import com.axecom.iweight.bean.OrderListResultBean;
import com.axecom.iweight.bean.PayNoticeBean;
import com.axecom.iweight.bean.ReportResultBean;
import com.axecom.iweight.bean.SaveGoodsReqBean;
import com.axecom.iweight.bean.ScalesCategoryGoods;
import com.axecom.iweight.bean.SettingDataBean;
import com.axecom.iweight.bean.SubOrderBean;
import com.axecom.iweight.bean.SubOrderReqBean;
import com.axecom.iweight.bean.UnusualOrdersBean;
import com.axecom.iweight.bean.VersionBean;
import com.axecom.iweight.bean.WeightBean;

import java.util.List;

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

    /**
     * 获取设备Id
     *
     * @param mac 设备mac地址
     * @return
     */
    @POST("getScalesIdByMac")
    Observable<BaseEntity<WeightBean>> getScalesIdByMac(@Query("mac") String mac);

    /**
     * 用户登录
     *
     * @param scalesId
     * @param serialNumber
     * @param password
     * @return
     */
    @POST("clientLogin")
    Observable<BaseEntity<LoginData>> clientLogin(@Query("scales_id") String scalesId, @Query("serial_number") String serialNumber, @Query("password") String password);

    /**
     * 司磅员登录
     *
     * @param scalesId
     * @param serialNumber
     * @param password
     * @return
     */
    @POST("staffMemberLogin")
    Observable<BaseEntity<LoginData>> staffMemberLogin(@Query("scales_id") String scalesId, @Query("serial_number") String serialNumber, @Query("password") String password);

    /**
     * 标定记录
     *
     * @param calibrationBean
     * @return
     */
    @POST("storeCalibrationRecord")
    Observable<BaseEntity> storeCalibrationRecord(@Body CalibrationBean calibrationBean);

    /**
     * 上报服务器智能秤在线
     *
     * @param scalesId
     * @return
     */
    @POST("isOnline")
    Observable<BaseEntity> isOnline(@Query("token") String token, @Query("scales_id") String scalesId);

    /**
     * 测试连接服务器
     *
     * @return
     */
    @GET("testConnection")
    Observable<BaseEntity> testConnection();

    /**
     * 获取商品
     *
     * @param token
     * @param mac
     * @return
     */
    @POST("getScalesCategoryGoods")
    Observable<BaseEntity<ScalesCategoryGoods>> getScalesCategoryGoods(@Query("token") String token, @Query("mac") String mac);

    /**
     * 获取商品
     *
     * @param token
     * @param mac
     * @return
     */
    @POST("getGoodsData")
    Observable<BaseEntity<ScalesCategoryGoods>> getGoodsData(@Query("token") String token, @Query("mac") String mac);

    /**
     * 提交订单
     *
     * @param subOrderReqBean
     * @return
     */
    @POST("submitOrder")
    Observable<BaseEntity<SubOrderBean>> submitOrder(@Body SubOrderReqBean subOrderReqBean);

    /**
     * 获取系统设置数据
     *
     * @param mac
     * @return
     */
    @POST("getSettingData")
    Observable<BaseEntity> getSettingData(@Query("token") String token, @Query("mac") String mac);

    /**
     * 日报表与月报表
     *
     * @param mac
     * @param dateVal
     * @param typeVal
     * @param page
     * @param pageNum
     * @return
     */
    @POST("getReportsList")
    Observable<BaseEntity<ReportResultBean>> getReportsList(@Query("token") String token, @Query("mac") String mac, @Query("dateVal") String dateVal, @Query("typeVal") String typeVal, @Query("page") String page, @Query("pageNum") String pageNum);

    /**
     * 数据汇总 / 销售明细
     *
     * @param mac
     * @param dateVal
     * @param page
     * @param pageNum
     * @return
     */
    @POST("getOrderList")
    Observable<BaseEntity<OrderListResultBean>> getOrderList(@Query("token") String token, @Query("mac") String mac, @Query("dateVal") String dateVal, @Query("page") String page, @Query("pageNum") String pageNum);

    @POST("getScalesSettingData")
    Observable<BaseEntity<LocalSettingsBean>> getScalesSettingData(@Query("token") String token, @Query("mac") String mac);

    @POST("getOrders")
    Observable<BaseEntity<UnusualOrdersBean>> getOrders(@Query("token") String token, @Query("mac") String mac, @Query("page") String page, @Query("pageNum") String pageNum, @Query("typeVal") String typeVal);

    @POST("storeGoodsData")
    Observable<BaseEntity> storeGoodsData(@Body SaveGoodsReqBean goodsReqBean);

    @POST("invalidOrders")
    Observable<BaseEntity> invalidOrders(@Query("token") String token, @Query("mac") String mac, @Query("orderNo") String orderNo);

    @POST("getLoginInfo")
    Observable<BaseEntity<LoginInfo>> getLoginInfo(@Query("token") String token, @Query("mac") String mac);

    @GET("getVersion")
    Observable<BaseEntity<VersionBean>> getVersion();

    @POST("getPayNotice")
    Observable<BaseEntity<PayNoticeBean>> getPayNotice(@Query("order_no") String order_no);
}
