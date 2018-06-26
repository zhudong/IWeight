package com.axecom.iweight.manager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jb.sdk.aidl.JBPrinterConnectCallback;
import com.jb.sdk.aidl.JBPrinterRealStatusCallback;
import com.jb.sdk.aidl.JBService;
import com.jb.sdk.command.GpUtils;
import com.jb.sdk.command.ReceiptCommand;
import com.jb.sdk.io.GpDevice;
import com.jb.sdk.io.GpPort;
import com.jb.sdk.io.PortParameter;
import com.jb.sdk.service.JbPrintService;

import java.util.Vector;

/**
 * Created by Administrator on 2018-5-28.
 */

public class GPprinterManager {
    private static Context context;
    private int mPrinterId = 1;
    private static final int REQUEST_TOAST_PRINTER_STATUS = 1;


    public GPprinterManager(Context context){
        this.context = context;
           /* 绑定服务，绑定成功后调用ServiceConnection中的onServiceConnected方法 */
        Intent intent = new Intent(context, JbPrintService.class);
        context.bindService(intent, mServiceConnection, context.BIND_AUTO_CREATE);
    }


    private static final String TAG = "ServiceConnection";
    private JBService mService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "========onServiceConnected=======");
            mService = JBService.Stub.asInterface(service);
            try {
                mService.registerConnectCallback(new ConnectCallback());
                mService.registerPrinterStatusCallback(new QueryPrinterRealStatus());
                Log.d(TAG, "绑定服务成功了");
//                Toast.makeText(context, "绑定服务成功了", Toast.LENGTH_SHORT).show();;

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "========onServiceDisconnected=======");
            Toast.makeText(context, "onServiceDisconnected", Toast.LENGTH_SHORT).show();;

            mService = null;
        }
    };

    /**
     * 打印机连接回调
     */
    public class ConnectCallback extends JBPrinterConnectCallback.Stub {

        @Override
        public void onConnecting(final int mId) throws RemoteException {

            Log.d(TAG, "--------onConnecting----------");
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, getStatus(mId), Toast.LENGTH_SHORT).show();;
                }
            });
        }

        @Override
        public void onDisconnect(final int mId) throws RemoteException {
            Log.d(TAG, "--------onDisconnect----------");
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, getStatus(mId), Toast.LENGTH_SHORT).show();;

                }
            });
        }

        @Override
        public void onConnected(final int mId) throws RemoteException {
            Log.d(TAG, "--------onConnected----------");
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, getStatus(mId), Toast.LENGTH_SHORT).show();;
                }
            });
        }

//        @Override
//        public IBinder asBinder() {
//            return null;
//        }
    }
    public static byte[] ByteTo_byte(Vector<Byte> vector) {
        int len = vector.size();
        byte[] data = new byte[len];

        for(int i = 0; i < len; ++i) {
            data[i] = ((Byte)vector.get(i)).byteValue();
        }

        return data;
    }

    public void printTest(){
        Toast.makeText(context, "printTest", Toast.LENGTH_SHORT).show();;
        ReceiptCommand esc = new ReceiptCommand();
        esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);//设置打印左对齐
        esc.addText("Print text\n"); // 打印文字
        esc.addText("Welcome to use Gprinter!\n"); // 打印文字
        esc.addText("你好吗", "utf-8"); // 打印文字

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[]       bytes = ByteTo_byte(datas);
        String       sss   = Base64.encodeToString(bytes, Base64.DEFAULT);
        int          rs;
        try {
            mService.sendReceiptCommand(0, bytes);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getStatus(int id) {
        if (mService == null) {
            return "未连接";
        }
        int state = GpPort.STATE_NONE;
        try {
            state = mService.getPrinterConnectStatus(mPrinterId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        String str;
        if (state == GpPort.STATE_CONNECTED) {
            str = "已连接";
        } else if (state == GpPort.STATE_CONNECTING) {
            str = "链接中";
        } else {
            str = "未连接";
        }
        return str;
    }

    /**
     * 打开连接
     *
     */
    public void openConnect() {
        //id为打印服务操作的打印机的id，最大可以操作3台
        try {
            Toast.makeText(context, "openConnect", Toast.LENGTH_SHORT).show();;
            mService.openPort(mPrinterId, PortParameter.USB, "/dev/bus/usb/001/003", 0);
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
        context.unbindService(mServiceConnection);
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
                    Toast.makeText(context,(type == GpDevice.UNKNOWN_COMMAND ? "未知类型" :
                            (type == GpDevice.RECEIPT_COMMAND ? "票据模式" :
                                    "标签模式")), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    /**
     * 打印机实时状态查询回调
     */
    public class QueryPrinterRealStatus extends JBPrinterRealStatusCallback.Stub {

        @Override
        public void onPrinterRealStatus(int mId, int status, int requestCode) throws RemoteException {
            switch (requestCode) {
                case REQUEST_TOAST_PRINTER_STATUS:
                    showPrinterStatus(mId, status);
                    break;
            }
        }

//        @Override
//        public IBinder asBinder() {
//            return null;
//        }
    }


    /**
     * 显示打印机的实时状态
     *
     * @param id
     * @param status
     */
    private void showPrinterStatus(final int id, int status) {
        String str;
        if (status == GpDevice.STATE_NO_ERR) {
            str = "打印机正常";
        } else {
            str = "打印机 ";
            if ((byte) (status & GpDevice.STATE_OFFLINE) > 0) {
                str += "脱机";
            }
            if ((byte) (status & GpDevice.STATE_PAPER_ERR) > 0) {
                str += "缺纸";
            }
            if ((byte) (status & GpDevice.STATE_COVER_OPEN) > 0) {
                str += "打印机开盖";
            }
            if ((byte) (status & GpDevice.STATE_ERR_OCCURS) > 0) {
                str += "打印机出错";
            }
            if ((byte) (status & GpDevice.STATE_TIMES_OUT) > 0) {
                str += "查询超时";
            }
        }

        final String statusStr = str;
        ((Activity)context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context,
                        "打印机：" + id + " 状态：" + statusStr, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
