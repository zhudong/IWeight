package com.axecom.iweight.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;


import com.axecom.iweight.R;
import com.axecom.iweight.manager.GPprinterManager;
import com.axecom.iweight.utils.LogUtils;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Longer on 2016/10/26.
 */
public class SysApplication extends MultiDexApplication {
    public static String DEFAULT_LANGUAGE = "zh_CN";
    public static Context mContext;
    public static Handler mHandler;
    public static long mMainThreadId;
    public static SysApplication instance;
    private List<String> mSerachHistoryList;
    private int qtyCount;
    private String orderNumber;
    private int shippingScheme;
    private int paymentRequestCode;
    private String giftCardOrderNo;
    private long giftCardOrderId;
    private long parcelId;
    private int orderType;
    public static String machineCode;
    public static int mWidthPixels;
    public static int mHeightPixels;
    public GPprinterManager gPprinterManager;
    private UsbSerialDriver gpDriver;
    private UsbDevice usbDevice;

    private static DisplayImageOptions options02;


    //    当前发起付款的包裹
//    public static String mCurrentPaingParcel = "";

    public static int uIn;

    //    device_token
    public static String mDeviceToken = "";
    private static ImageLoader mImageLoader;

    //    当前请求支付的页面
    public static String mCurrentRequestPayment = "";

    public static Context getContext() {
        return mContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }

    public static String uuid = "1234567890";

    @Override
    public void onCreate() {// 程序的入口方法

        instance = this;

        // 1.上下文
        mContext = getApplicationContext();

        // 2.主线程的Handler
        mHandler = new Handler();

        // 3.得到主线程的id
        mMainThreadId = android.os.Process.myTid();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
            }
        });
        FlowManager.init(new FlowConfig.Builder(this).build());

        //初始化Facebook登录
        mImageLoader = ImageLoader.getInstance();

        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        mSerachHistoryList = new ArrayList<>();
        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        options02 = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).displayer(new FadeInBitmapDisplayer(100)).displayer(new RoundedBitmapDisplayer(10)).cacheInMemory(true).cacheOnDisk(true).showImageOnLoading(R.drawable.shape_bg_for_home_head).build();
        super.onCreate();
    }

    public static SysApplication getInstances() {
        return instance;
    }


    public void removeActivity(Activity activity) {

        for (Activity ac : mList) {
            if (ac.equals(activity)) {
                mList.remove(ac);
                break;
            }
        }
    }

    private List<Activity> mList = new LinkedList<Activity>();


    public List<String> getSerachHistoryList() {
        return mSerachHistoryList;
    }

    public String getMachineCode() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        LogUtils.e("这是机器码:" + uniqueId);
        return uniqueId;
    }

    private String getChanl() {
        String channel = "";
        try {
            channel = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.getString("UMENG_CHANNEL");
            //LogUtils.e("这是渠道号:" + channel);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public static String getRandoomUUID() {
        return UUID.randomUUID().toString();
    }

    public static DisplayImageOptions getRoundImageLoaderOption() {

        return options02;
    }

    public UsbSerialDriver getGpDriver() {
        return gpDriver;
    }

    public void setGpDriver(UsbSerialDriver gpDriver) {
        this.gpDriver = gpDriver;
    }

    public UsbDevice getCardDevice() {
        return usbDevice;
    }

    public void setCardDevice(UsbDevice cardDevice) {
        this.usbDevice = cardDevice;
    }
}
