package com.axecom.iweight.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;

import java.util.Map;
import java.util.Vector;

/**
 * Created by Administrator on 2018-5-28.
 */

public class GPprinterManager {
    private static Context context;
    private int mPrinterId = 1;
    private static final int REQUEST_TOAST_PRINTER_STATUS = 1;


    private static final String TAG = "ServiceConnection";
    private GpService mService;
    private PrinterServiceConnection conn = null;

    public GPprinterManager(Context context){
        this.context = context;
           /* 绑定服务，绑定成功后调用ServiceConnection中的onServiceConnected方法 */
        conn = new PrinterServiceConnection();
        registerBroadcast();

        Intent intent = new Intent(context, GpPrintService.class);
        context.bindService(intent, conn, context.BIND_AUTO_CREATE);
    }

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = GpService.Stub.asInterface(service);
        }
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GpCom.ACTION_CONNECT_STATUS);
        context.registerReceiver(PrinterStatusBroadcastReceiver, filter);
    }
    /**
     * 打印机连接回调
     */

    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (GpCom.ACTION_CONNECT_STATUS.equals(intent.getAction())) {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                Log.d(TAG, "connect status " + type);
                if (type == GpDevice.STATE_CONNECTING) {

                } else if (type == GpDevice.STATE_NONE) {
                } else if (type == GpDevice.STATE_VALID_PRINTER) {
                } else if (type == GpDevice.STATE_INVALID_PRINTER) {
                }
            }
        }
    };
//        @Override
//        public IBinder asBinder() {
//            return null;
//        }
    public static byte[] ByteTo_byte(Vector<Byte> vector) {
        int len = vector.size();
        byte[] data = new byte[len];

        for(int i = 0; i < len; ++i) {
            data[i] = ((Byte)vector.get(i)).byteValue();
        }

        return data;
    }

    public void printTest(){

    }


    /**
     * 打开连接
     *
     */
    public void openConnect() {
        //id为打印服务操作的打印机的id，最大可以操作3台
        try {
            Toast.makeText(context, "openConnect", Toast.LENGTH_SHORT).show();;
            mService.openPort(mPrinterId, PortParameters.USB, "/dev/bus/usb/001/009", 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打印测试页
     *
     */
    public void printTestPaper() {
        try {
            mService.printeTestPage(mPrinterId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     *
     */
    public void closeConnect() {
        if (mService != null) {
            try {
                mService.closePort(mPrinterId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        context.unbindService(conn);
    }

    /**
     * 获取命令类型
     *
     * @param view
     */
    public void getCommandTypes(View view) {
        try {
            final int type = mService.getPrinterCommandType(mPrinterId);
            ((Activity)context).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

}
