package com.shangtongyin.tools.serialport;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.luofx.utils.log.MyLog;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;


import android_serialport_api.PosPort;

/**
 * luofaxin 20180829 修改
 * 商通SY2000 称重模块 重量读取帮助类
 */
public class WeightHelper implements IConstants_ST {

//    public int getBaudrate() {
//        return baudrate;
//    }
//    public String getPath() {
//        return path;
//    }

    //    public void setBaudrate(int baudrate) {
//        this.baudrate = baudrate;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }


    private Handler handler;

    public WeightHelper(Handler handler) {
//        this.readWeightClick = readWeightClick;
        this.handler = handler;
    }

    /**
     * 称的称重模块串口名称 ，波特率 9600
     */
//    private String path = "/dev/ttyS0";
//    private int baudrate = 9600;

//    private ReadWeightClick readWeightClick;


    private final int TIMERDELAY = 150;
    private int READDELAY_50 = 50;

    /**
     * 端口实例
     */
    private PosPort mSerialPort = null;
    /**
     * 读取重量的命令
     */
    private byte[] readWeightOrder = new byte[]{0x55, (byte) 0xAA};

    ReadThread mThread;
    GetWeightThreadForST gThread;
    private boolean isRun;

    /**
     * @return 开启式否成功
     */
    public boolean open() {
        try {
            mSerialPort = getSerialPortPrinter();
            if (mSerialPort.getOutputStream() == null || mSerialPort.getInputStream() == null) {
                return false;
            }
            isRun = true;
            mThread = new ReadThread();
            mThread.start();
            gThread = new GetWeightThreadForST();
            gThread.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获得打印机的串口 控制
     *
     * @return 打印机串口
     */
    public PosPort getSerialPortPrinter() throws SecurityException,
            IOException, InvalidParameterException {
        if (mSerialPort == null) {
            mSerialPort = new PosPort(new File("/dev/ttyS0"), 9600, 0);
        }
        return mSerialPort;
    }

    //关闭
    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        mSerialPort = null;

        if (gThread != null) {
            gThread.stopRun();
        }
        if (mThread != null) {
            mThread.stopRun();
        }
    }

    public class GetWeightThreadForST extends Thread {
        private boolean isRun; //是否一直循环

        @Override
        public synchronized void start() {
            isRun = true;
            super.start();
        }

        public void stopRun() {
            isRun = false;
            interrupt();
        }

        @Override
        public void run() {
            super.run();
            while (isRun) {
                try {
                    getSerialPortPrinter().getOutputStream().write(readWeightOrder);
                    sleep(TIMERDELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 重量数据读取线程
     */
    private class ReadThread extends Thread {
        private boolean isRun;
        byte[] buffer = new byte[64];
        private int size;

        public ReadThread() {
            this.isRun = true;
        }

        public void stopRun() {
            this.isRun = false;
        }

        @Override
        public void run() {
            super.run();
            while (isRun) {
                //发出读取重量的命令   getSerialPortPrinter().getOutputStream().write(readWeightOrder);
                //休眠100ms, 然后读取重量信息数据 ,此方法不可靠
                try {
                    if (mSerialPort != null) {
                        size = getSerialPortPrinter().getInputStream().read(buffer);
                        if (size > 0) {
                            String str = new String(buffer, 0, size).trim();
                            if (str.contains("kg")) {
                                int index = str.indexOf("kg");
                                str = str.substring(0, index).trim();
                                if (!TextUtils.isEmpty(str)) {
//                                    double b = Double.parseDouble(str);
                                    Message msg = handler.obtainMessage(NOTIFY_WEIGHT, str);
                                    handler.sendMessage(msg);
//                                    readWeightClick.readData(str);
                                }
                            }
                        }
                    }
                    Thread.sleep(READDELAY_50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    protected abstract void onDataReceived(double d);

    /**
     * 称重模块置零 命令
     */
    public void resetBalance() {
        try {
            byte[] order = new byte[]{0x55, 0x00};
            getSerialPortPrinter().getOutputStream().write(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 置0
     */
    public void resetBalance(byte[] order) {
        try {
            getSerialPortPrinter().getOutputStream().write(order);
            MyLog.myInfo("getOutputStream");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void suspend() {
        if (gThread != null) {
            gThread.stopRun();
            gThread.interrupt();
            gThread = null;
        }
    }

    public void restart() {
        gThread = new GetWeightThreadForST();
        gThread.start();
    }


}
