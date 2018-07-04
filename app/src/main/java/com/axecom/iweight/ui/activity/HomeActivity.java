package com.axecom.iweight.ui.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.base.SysApplication;
import com.axecom.iweight.bean.LoginData;
import com.axecom.iweight.bean.WeightBean;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.manager.GPprinterManager;
import com.axecom.iweight.manager.MacManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.view.SoftKeyborad;
import com.axecom.iweight.utils.LogUtils;
import com.axecom.iweight.utils.SerialPortUtils;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.syc.function.Function;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018-5-8.
 */

public class HomeActivity extends BaseActivity {

    private View rootView;
    private Button confirmBtn;
    private TextView cardNumberTv;
    private TextView pwdTv;
    private TextView loginTv;
    private TextView weightTv;
    private int weightId;
    private CheckedTextView savePwdCtv;
    private int commHandle = 0;
    private GPprinterManager gPprinterManager;


    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        confirmBtn = rootView.findViewById(R.id.home_confirm_btn);
        cardNumberTv = rootView.findViewById(R.id.home_card_number_tv);
        pwdTv = rootView.findViewById(R.id.home_pwd_tv);
        loginTv = rootView.findViewById(R.id.home_login_tv);
        weightTv = rootView.findViewById(R.id.home_weight_number_tv);
        savePwdCtv = rootView.findViewById(R.id.home_save_pwd_ctv);

        pwdTv.setOnClickListener(this);
        loginTv.setOnClickListener(this);
        cardNumberTv.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        savePwdCtv.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
//        getScalesIdByMac(MacManager.getInstace(HomeActivity.this).getMac());
        getScalesIdByMac(Constants.MAC_TEST);
//        commHandle = Function.API_OpenComm("dev/tty".getBytes(), 115200);
//        if (commHandle == 0) {
//            Toast.makeText(this, "can't open serial", Toast.LENGTH_SHORT).show();
//        }
        usbOpen();
    }

    public void usbOpen(){
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }

        for (int i = 0; i < availableDrivers.size(); i++) {
            UsbSerialDriver driver = availableDrivers.get(i);
            if(driver.getDevice().getVendorId() == 6790 && driver.getDevice().getProductId() == 29987){
                UsbDeviceConnection connection = null;

                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
                if(manager.hasPermission(driver.getDevice())){
                    //if has already got permission, just goto connect it
                    //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
                    //and also choose option: not ask again
                    connection = manager.openDevice(driver.getDevice());
                }else{
                    //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
                    manager.requestPermission(driver.getDevice(), mPermissionIntent);
                }
                if (connection == null) {
                    // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
                    return;
                }
                port = driver.getPorts().get(0);
                try {
                    port.open(connection);
                    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                    new ReadThread(port).start();
                } catch (IOException e) {
                    // Deal with error.
                } finally {
                }
            }
            if(driver.getDevice().getVendorId() == 26728 && driver.getDevice().getProductId() == 1280){
                SysApplication.getInstances().setGpDriver(driver);
            }
        }


// Read some data! Most have just one port (port 0).

    }
    public boolean threadStatus = false; //线程状态，为了安全终止线程
    UsbSerialPort port;
    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread{
        UsbSerialPort port;
        public ReadThread(    UsbSerialPort port){
            this.port = port;
        }
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus){
                LogUtils.d( "进入线程run");
                //64   1024
                byte[] buffer = new byte[32];
                int size; //读取数据的大小
                try {
                    int numBytesRead = port.read(buffer, 1000);
                    String s="" ;
                    String s1 = "";
                    for (byte b : buffer) {
                        String s2 = String.format("%02x ", b).substring(0,2);
                        s1+=Integer.parseInt(String.format("%02x ", b).substring(0,2), 16);
                        s += String.format("%02x ", b);

                    }
                    LogUtils.d("Read " + s1 + " bytes.");

                } catch (IOException e) {
                    LogUtils.e( "run: 数据读取异常：" +e.toString());
                }
            }

        }
    }
    public static int byte2Int(byte b){
        int r = (int) b;
        return r;
    }

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    isOnline();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            /**
             * 此处执行任务
             * */
            mHanlder.sendEmptyMessage(1);
            mHanlder.postDelayed(this, 60 * 1000 * 5);//延迟5秒,再次执行task本身,实现了循环的效果
        }
    };

    @Override
    public void onClick(View v) {
        SoftKeyborad.Builder builder = new SoftKeyborad.Builder(HomeActivity.this);
        switch (v.getId()) {
            case R.id.home_login_tv:
            case R.id.home_confirm_btn:
//                getScalesIdByMac(MacManager.getInstace(HomeActivity.this).getMac());
                clientLogin(weightId + "", cardNumberTv.getText().toString(), pwdTv.getText().toString());
//                clientLogin("4", cardNumberTv.getText().toString(), pwdTv.getText().toString());
                break;
            case R.id.home_card_number_tv:
                builder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        cardNumberTv.setText(result);
                        if (AccountManager.getInstance().getPwdBySerialNumber(result) != null){
                            pwdTv.setText(AccountManager.getInstance().getPwdBySerialNumber(result));
                            savePwdCtv.setChecked(true);
                        }else {
                            savePwdCtv.setChecked(false);
                        }
                    }
                }).show();
                break;
            case R.id.home_pwd_tv:
                builder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        pwdTv.setText(result);
                    }
                }).show();
                break;
            case R.id.home_save_pwd_ctv:
                savePwdCtv.setChecked(!savePwdCtv.isChecked());
                break;
        }
    }

    public void getScalesIdByMac(String mac) {
        RetrofitFactory.getInstance().API()
                .getScalesIdByMac(mac)
                .compose(this.<BaseEntity<WeightBean>>setThread())
                .subscribe(new Observer<BaseEntity<WeightBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(final BaseEntity<WeightBean> baseEntity) {
                        if (baseEntity.isSuccess()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    weightId = baseEntity.getData().getId();
                                    weightTv.setText(weightId + "");
                                    AccountManager.getInstance().saveScalesId(weightId + "");
                                }
                            });
                        }
                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
                        closeLoading();

                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });
    }

    public void clientLogin(String scalesId, final String serialNumber, final String password) {
        RetrofitFactory.getInstance().API()
                .clientLogin(scalesId, serialNumber, password)
                .compose(this.<BaseEntity<LoginData>>setThread())
                .subscribe(new Observer<BaseEntity<LoginData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();

                    }

                    @Override
                    public void onNext(BaseEntity<LoginData> loginDataBaseEntity) {
                        if (loginDataBaseEntity.isSuccess()) {
                            AccountManager.getInstance().saveToken(loginDataBaseEntity.getData().getToken());
                            if (savePwdCtv.isChecked()) {
                                AccountManager.getInstance().savePwd(serialNumber, password);
                            } else {
                                AccountManager.getInstance().savePwd(serialNumber, null);
                            }
                            mHanlder.postDelayed(task, 1000);
//                            AccountManager.getInstance().savePwdChecked(serialNumber, savePwdCtv.isChecked());
                            startDDMActivity(MainActivity.class, false);
                        } else {
                            showLoading(loginDataBaseEntity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeLoading();

                    }

                    @Override
                    public void onComplete() {
                        closeLoading();

                    }
                });
    }

    public void isOnline() {
        RetrofitFactory.getInstance().API()
                .isOnline(AccountManager.getInstance().getToken(), AccountManager.getInstance().getScalesId())
                .compose(this.<BaseEntity>setThread())
                .subscribe(new Observer<BaseEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(BaseEntity baseEntity) {
                        if (baseEntity.isSuccess()) {

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        closeLoading();
                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }


}
