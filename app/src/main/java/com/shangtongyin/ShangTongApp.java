package com.shangtongyin;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.axecom.iweight.ui.activity.HomeActivity;
import com.luofx.utils.PreferenceUtils;
import com.shangtongyin.tools.serialport.Print;
import com.shangtongyin.tools.serialport.USB_Print;

/**
 * Fun：
 * Author：Linus_Xie
 * Date：2018/8/2 14:55
 */
public class ShangTongApp extends MultiDexApplication {


    /**
     * 0:默认值没有选着打印机    1"佳博2120TF", 2 "美团打印机", 3 "商通打印机", 4 "香山打印机
     */
    private int printIndex;

    public int getPrintIndex() {
        return printIndex;
    }

    public void setPrintIndex(int printIndex) {
        this.printIndex = printIndex;
    }

    private Print print;

    public Print getPrint() {
        return print;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        print = new Print();
        print.open();
//        registerBroadcast();
        USB_Print.initPrinter(this);
        SharedPreferences sharedPreferences = PreferenceUtils.getSp(this);
        this.printIndex = sharedPreferences.getInt("printIndex", 0);
    }

    /**
     * 最终关闭串口
     */
    public void onDestory() {
        if (print != null) {
            print.closeSerialPort();
        }
    }
}
