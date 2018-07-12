package com.axecom.iweight.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.base.SysApplication;
import com.axecom.iweight.bean.LoginInfo;
import com.axecom.iweight.bean.ScalesCategoryGoods;
import com.axecom.iweight.bean.SubOrderBean;
import com.axecom.iweight.bean.SubOrderReqBean;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.manager.ClientManager;
import com.axecom.iweight.manager.GPprinterManager;
import com.axecom.iweight.manager.MacManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.adapter.DigitalAdapter;
import com.axecom.iweight.ui.adapter.GridAdapter;
import com.axecom.iweight.ui.view.BTHelperDialog;
import com.axecom.iweight.ui.view.BluetoothDialog;
import com.axecom.iweight.utils.LogUtils;
import com.axecom.iweight.utils.SPUtils;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.command.LabelCommand;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.OnSearchDeviceListener;

import static com.axecom.iweight.base.SysApplication.mContext;
import static com.axecom.iweight.ui.uiutils.UIUtils.getResources;
import static com.nostra13.universalimageloader.utils.IoUtils.DEFAULT_BUFFER_SIZE;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String KEY_ISHOW_COUNT_LAYOUT = "key_ishow_count_layout";
    private static final String[] DATA_DIGITAL = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "删除", "0", "."};
    private GPprinterManager gPprinterManager;
    private PrinterServiceConnection conn = null;
    private UsbSerialDriver driver;
    private View rootView;

    private GridView commoditysGridView;
    private GridView digitalGridView;
    private ListView commoditysListView;
    private CommodityAdapter commodityAdapter;
    private GridAdapter gridAdapter;
    private DigitalAdapter digitalAdapter;
    //    private Button bankCardBtn;
    private Button cashBtn;
    private Button settingsBtn;
    private Button mainClearBtn;

    private LinearLayout weightLayout;
    private LinearLayout countLayout;
    private EditText countEt;
    private EditText priceEt;
    private Button clearBtn, addBtn;
    private TextView commodityNameTv;
    private TextView grandTotalTv;
    private TextView weightTotalTv;
    private TextView weightTv;
    private TextView priceTotalTv;
    private TextView operatorTv;
    private TextView stallNumberTv;
    private TextView componyTitleTv;
    private TextView weightTopTv;
    private List<ScalesCategoryGoods.HotKeyGoods> hotKeyGoodsList;
    private List<ScalesCategoryGoods.HotKeyGoods> seledtedGoodsList;
    private ScalesCategoryGoods.HotKeyGoods selectedGoods;
    private int mTotalCopies = 0;


    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);

        commoditysGridView = rootView.findViewById(R.id.main_commoditys_grid_view);
        digitalGridView = rootView.findViewById(R.id.main_digital_keys_grid_view);
        commoditysListView = rootView.findViewById(R.id.main_commoditys_list_view);
//        bankCardBtn = rootView.findViewById(R.id.main_bank_card_btn);
        weightLayout = rootView.findViewById(R.id.main_weight_layout);
        countLayout = rootView.findViewById(R.id.main_count_layout);
        countEt = rootView.findViewById(R.id.main_count_et);
        commodityNameTv = rootView.findViewById(R.id.main_commodity_name_tv);
        grandTotalTv = rootView.findViewById(R.id.main_grandtotal_tv);
        weightTotalTv = rootView.findViewById(R.id.main_weight_total_tv);
        weightTv = rootView.findViewById(R.id.main_weight_tv);
        operatorTv = rootView.findViewById(R.id.main_operator_tv);
        stallNumberTv = rootView.findViewById(R.id.main_stall_number_tv);
        componyTitleTv = rootView.findViewById(R.id.main_compony_title_tv);
        priceTotalTv = rootView.findViewById(R.id.main_price_total_tv);
        weightTopTv = rootView.findViewById(R.id.main_weight_top_tv);
        cashBtn = rootView.findViewById(R.id.main_cash_btn);
        settingsBtn = rootView.findViewById(R.id.main_settings_btn);
        mainClearBtn = rootView.findViewById(R.id.main_clear_btn);
        clearBtn = rootView.findViewById(R.id.main_digital_clear_btn);
        addBtn = rootView.findViewById(R.id.main_digital_add_btn);
        priceEt = rootView.findViewById(R.id.main_commodity_price_et);
        priceEt.requestFocus();
        disableShowInput(priceEt);

//        gPprinterManager = new GPprinterManager(this);
        connection();

//        gPprinterManager.openConnect();
//        bankCardBtn.setOnClickListener(this);

        mainClearBtn.setOnClickListener(this);
        cashBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        weightTopTv.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        getGoodsData();
        getLoginInfo();
        hotKeyGoodsList = new ArrayList<>();
        seledtedGoodsList = new ArrayList<>();
        commodityAdapter = new CommodityAdapter(this, seledtedGoodsList);
        commoditysListView.setAdapter(commodityAdapter);

        gridAdapter = new GridAdapter(this, hotKeyGoodsList);
        commoditysGridView.setAdapter(gridAdapter);
        commoditysGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (!mThread.isAlive()) {
//                    mThread.run();
//                }
                selectedGoods = new ScalesCategoryGoods.HotKeyGoods();
                selectedGoods.id = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).id;
                selectedGoods.cid = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).cid;
                selectedGoods.price = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).price;
                selectedGoods.traceable_code = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).traceable_code;
                selectedGoods.is_default = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).is_default;
                selectedGoods.name = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).name;
                commodityNameTv.setText(selectedGoods.name);
            }
        });

        List<String> digitaList = new ArrayList<>();

        for (int i = 0; i < DATA_DIGITAL.length; i++) {
            digitaList.add(DATA_DIGITAL[i]);
        }
        digitalAdapter = new DigitalAdapter(this, digitaList);
        digitalGridView.setAdapter(digitalAdapter);
        digitalGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedGoods == null) {
                    return;
                }
                String text = parent.getAdapter().getItem(position).toString();
                setEditText(priceEt, position, text);
                grandTotalTv.setText(priceEt.getText().toString() + " 元");
                selectedGoods.price = priceEt.getText().toString();
                selectedGoods.grandTotal = priceEt.getText().toString();
            }
        });
        // 注册实时状态查询广播
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        /**
         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
        /**
         * 标签模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus(RESPONSE_MODE mode)
         * ，在打印完成后会接收到，action为GpCom.ACTION_LABEL_RESPONSE的广播，特别用于连续打印，
         * 可参照该sample中的sendLabelWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_LABEL_RESPONSE));
        driver = SysApplication.getInstances().getGpDriver();
        if (driver != null) {
//            try {
//                mGpService.openPort(rootView.getId(), PortParameters.USB, driver.getDevice().getDeviceName(), 0);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//            SysApplication.getInstances().gPprinterManager.openConnect(driver.getDevice().getDeviceName());
        }
//
//        if(!ClientManager.getClient().isBluetoothOpened()){
//            ClientManager.getClient().openBluetooth();
//        }
//
//
//        BTHelperDialog.Builder builder = new  BTHelperDialog.Builder(this);
//        builder.create(new BTHelperDialog.OnBtnClickListener() {
//
//            @Override
//            public void onConfirmed(BtHelperClient.STATUS mCurrStatus) {
//                mThread.run();
//
//            }
//
//            @Override
//            public void onCanceled(String result) {
//
//            }
//        }).show();
//
//        test();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean switchSimpleOrComplex = (boolean) SPUtils.get(this, SettingsActivity.KET_SWITCH_SIMPLE_OR_COMPLEX, false);
        if(switchSimpleOrComplex){
            countLayout.setVisibility(View.VISIBLE);
            weightLayout.setVisibility(View.GONE);
        }else {
            countLayout.setVisibility(View.GONE);
            weightLayout.setVisibility(View.VISIBLE);
        }
        mTotalCopies = (int) SPUtils.get(this, LocalSettingsActivity.KEY_PRINTER_COUNT, mTotalCopies);
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            InputStream inputStream = BtHelperClient.from(MainActivity.this).mInputStream;
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while (true) {

                    try {
                        String s = reader.readLine();
                        String s1 = s;
                        Looper.prepare();
                        Toast.makeText(mContext, "weight " + s1 + " mInputStream.available() " + inputStream.available(), Toast.LENGTH_LONG).show();
//                        Thread.sleep(200);
                        if (inputStream.available() == 0) {
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }


                }
            }
        }
    });

    public void submitOrder(SubOrderReqBean subOrderReqBean) {
        RetrofitFactory.getInstance().API()
                .submitOrder(subOrderReqBean)
                .compose(this.<BaseEntity<SubOrderBean>>setThread())
                .subscribe(new Observer<BaseEntity<SubOrderBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();

                    }

                    @Override
                    public void onNext(BaseEntity<SubOrderBean> subOrderBeanBaseEntity) {
                        if (subOrderBeanBaseEntity.isSuccess()) {
                        } else {
                            showLoading(subOrderBeanBaseEntity.getMsg());
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
    protected void onDestroy() {
        super.onDestroy();
//        gPprinterManager.closeConnect();
        if (conn != null) {
            unbindService(conn); // unBindService
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            startDDMActivity(SettingsActivity.class, false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.main_bank_card_btn:
            case R.id.main_weight_top_tv:
                Intent intent = new Intent();
                intent.setClass(this, StaffMemberLoginActivity.class);
                startActivityForResult(intent, 1002);
                break;
            case R.id.main_cash_btn:
//                startDDMActivity(SettingsActivity.class, false);
                showDialog(v);
                break;
            case R.id.main_settings_btn:
                Intent intent2 = new Intent();
                intent2.setClass(this, StaffMemberLoginActivity.class);
                startActivityForResult(intent2, 1002);
                break;
            case R.id.main_clear_btn:


//                gPprinterManager.openConnect();
//                gPprinterManager.printTest();
                SubOrderReqBean subOrderReqBean = new SubOrderReqBean();
                SubOrderReqBean.Goods good;
                List<SubOrderReqBean.Goods> goodsList = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    good = new SubOrderReqBean.Goods();
                    good.setGoods_id("1");
                    good.setGoods_name("白菜" + i);
                    good.setGoods_price("0.50");
                    good.setGoods_number("2");
                    good.setGoods_amount("1.00");
                    goodsList.add(good);
                }
                subOrderReqBean.setToken(AccountManager.getInstance().getToken());
//                subOrderReqBean.setMac(MacManager.getInstace(this).getMac());
                subOrderReqBean.setMac(Constants.MAC_TEST);
                subOrderReqBean.setTotal_amount("1");
                subOrderReqBean.setTotal_weight("1kg");
                subOrderReqBean.setPayment_id("1");
                subOrderReqBean.setCreate_time(getCurrentTime());
                subOrderReqBean.setGoods(goodsList);
                clear(1);
//                submitOrder(subOrderReqBean);
                break;
            case R.id.main_digital_clear_btn:
                try {
                    int type = mGpService.getPrinterCommandType(mPrinterIndex);
                    if (type == GpCom.LABEL_COMMAND) {
                        mGpService.queryPrinterStatus(mPrinterIndex, 1000, REQUEST_PRINT_LABEL);
                    }
                    if (type == GpCom.ESC_COMMAND) {
                        mGpService.queryPrinterStatus(mPrinterIndex, 1000, REQUEST_PRINT_RECEIPT);
                    }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                priceEt.setText("");
                weightTv.setText("");
                weightTotalTv.setText("");
                grandTotalTv.setText("");
                break;
            case R.id.main_digital_add_btn:
                if (selectedGoods == null) {
                    return;
                }
                seledtedGoodsList.add(selectedGoods);
                commodityAdapter.notifyDataSetChanged();
                int weightTotal = 0;
                int priceTotal = 0;
                for (int i = 0; i < seledtedGoodsList.size(); i++) {
                    ScalesCategoryGoods.HotKeyGoods goods = seledtedGoodsList.get(i);
//                    weightTotal+=Integer.parseInt(goods.weight);
                    if (goods.price != null) {
                        if (!TextUtils.isEmpty(goods.price)) {
                            priceTotal += Integer.parseInt(goods.price);
                        }
                    }
                }
                weightTotalTv.setText(weightTotal + "");
                priceTotalTv.setText(priceTotal + "");
                clear(0);
                break;
        }
    }

    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private int mPrinterIndex = 0;
    private GpService mGpService = null;


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("TAG", action);
            // GpCom.ACTION_DEVICE_REAL_STATUS 为广播的IntentFilter
            if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {

                // 业务逻辑的请求码，对应哪里查询做什么操作
                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
                // 判断请求码，是则进行业务操作
                if (requestCode == MAIN_QUERY_PRINTER_STATUS) {

                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    String str;
                    if (status == GpCom.STATE_NO_ERR) {
                        str = "打印机正常";
                    } else {
                        str = "打印机 ";
                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                            str += "脱机";
                        }
                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                            str += "缺纸";
                        }
                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                            str += "打印机开盖";
                        }
                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                            str += "打印机出错";
                        }
                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                            str += "查询超时";
                        }
                    }

                    Toast.makeText(getApplicationContext(), "打印机：" + mPrinterIndex + " 状态：" + str, Toast.LENGTH_SHORT)
                            .show();
                } else if (requestCode == REQUEST_PRINT_LABEL) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status == GpCom.STATE_NO_ERR) {
//                        sendLabel();
                        sendLabel();
                    } else {
                        Toast.makeText(MainActivity.this, "query printer status error", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == REQUEST_PRINT_RECEIPT) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status == GpCom.STATE_NO_ERR) {
                        sendReceipt();
                    } else {
                        Toast.makeText(MainActivity.this, "query printer status error", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (action.equals(GpCom.ACTION_RECEIPT_RESPONSE)) {
//                if (--mTotalCopies > 0) {
//                    sendReceiptWithResponse();
//                }
            } else if (action.equals(GpCom.ACTION_LABEL_RESPONSE)) {
                byte[] data = intent.getByteArrayExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE);
                int cnt = intent.getIntExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE_CNT, 1);
                String d = new String(data, 0, cnt);
                /**
                 * 这里的d的内容根据RESPONSE_MODE去判断返回的内容去判断是否成功，具体可以查看标签编程手册SET
                 * RESPONSE指令
                 * 该sample中实现的是发一张就返回一次,这里返回的是{00,00001}。这里的对应{Status,######,ID}
                 * 所以我们需要取出STATUS
                 */
                Log.d("LABEL RESPONSE", d);

                if (--mTotalCopies > 0 && d.charAt(1) == 0x00) {
                    sendLabel();
                }
            }
        }
    };



    public void sendLabel() {
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(60, 60); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(0); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.BACKWARD, LabelCommand.MIRROR.NORMAL);// 设置打印方向
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区

//        tsc.addText(20, 30, LabelCommand.FONTTYPE.KOREAN, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
//                "조선말");
        tsc.addText(100, 30, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "深圳市安鑫宝科技发展有限公司");
//        tsc.addText(180, 30, LabelCommand.FONTTYPE.TRADITIONAL_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
//                "繁體字");

        // 绘制图片
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
//        tsc.addBitmap(20, 60, LabelCommand.BITMAP_MODE.OVERWRITE, b.getWidth(), b);
        //绘制二维码
        tsc.addQRCode(105, 75, LabelCommand.EEC.LEVEL_L, 5, LabelCommand.ROTATION.ROTATION_0, " www.smarnet.cc");
        // 绘制一维条码
//        tsc.add1DBarcode(50, 350, LabelCommand.BARCODETYPE.CODE128, 100, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "SMARNET");
        tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendLabelCommand(mPrinterIndex, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(this, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void sendReceipt() {

        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("Sample\n"); // 打印文字
        esc.addPrintAndLineFeed();

		/* 打印文字 */
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText("Print text\n"); // 打印文字
        esc.addText("Welcome to use SMARNET printer!\n"); // 打印文字

		/* 打印繁体中文 需要打印机支持繁体字库 */
        String message = "佳博智匯票據打印機\n";
        // esc.addText(message,"BIG5");
        esc.addText(message, "GB2312");
        esc.addPrintAndLineFeed();

		/* 绝对位置 具体详细信息请查看GP58编程手册 */
        esc.addText("智汇");
        esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
        esc.addSetAbsolutePrintPosition((short) 6);
        esc.addText("网络");
        esc.addSetAbsolutePrintPosition((short) 10);
        esc.addText("设备");
        esc.addPrintAndLineFeed();

		/* 打印图片 */
        esc.addText("Print bitmap!\n"); // 打印文字
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        esc.addRastBitImage(b, 384, 0); // 打印图片

		/* 打印一维条码 */
        esc.addText("Print code128\n"); // 打印文字
        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);//
        // 设置条码可识别字符位置在条码下方
        esc.addSetBarcodeHeight((byte) 60); // 设置条码高度为60点
        esc.addSetBarcodeWidth((byte) 1); // 设置条码单元宽度为1
        esc.addCODE128(esc.genCodeB("SMARNET")); // 打印Code128码
        esc.addPrintAndLineFeed();

		/*
		 * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
		 */
        esc.addText("Print QRcode\n"); // 打印文字
        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31); // 设置纠错等级
        esc.addSelectSizeOfModuleForQRCode((byte) 3);// 设置qrcode模块大小
        esc.addStoreQRCodeData("www.smarnet.cc");// 设置qrcode内容
        esc.addPrintQRCode();// 打印QRCode
        esc.addPrintAndLineFeed();

		/* 打印文字 */
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印左对齐
        esc.addText("Completed!\r\n"); // 打印结束
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
        esc.addPrintAndFeedLines((byte) 8);

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(mPrinterIndex, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
            try {
                if(mGpService != null)
                    if(driver != null)
                        mGpService.openPort(0, PortParameters.USB, driver.getDevice().getDeviceName(), 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public void clear(int type) {
        if (type == 0) {
            selectedGoods = null;
            grandTotalTv.setText("0.00");
            commodityNameTv.setText("");
            priceEt.setText("");
//            weightTv.setText("");
        }
        if (type == 1) {
            weightTopTv.setText("0.000公斤");
//            priceEt.setText("");
            weightTv.setText("");
            seledtedGoodsList.clear();
            commodityAdapter.notifyDataSetChanged();
        }
    }

    public void showDialog(View v) {
        switch (v.getId()) {
//            case R.id.main_bank_card_btn:
//                intent.setClass(this, UseBankCardActivity.class);
//                break;
            case R.id.main_cash_btn:
                Intent intent = new Intent();
                SubOrderReqBean subOrderReqBean = new SubOrderReqBean();
                SubOrderReqBean.Goods good;
                List<SubOrderReqBean.Goods> goodsList = new ArrayList<>();
                for (int i = 0; i < seledtedGoodsList.size(); i++) {
                    good = new SubOrderReqBean.Goods();
                    ScalesCategoryGoods.HotKeyGoods hotKeyGoods = seledtedGoodsList.get(i);
                    good.setGoods_id(hotKeyGoods.id + "");
                    good.setGoods_name(hotKeyGoods.name);
                    good.setGoods_price(hotKeyGoods.price);
                    good.setGoods_number("1");
                    good.setGoods_amount(hotKeyGoods.grandTotal);
                    goodsList.add(good);
                }
                subOrderReqBean.setToken(AccountManager.getInstance().getToken());
//                subOrderReqBean.setMac(MacManager.getInstace(this).getMac());
                subOrderReqBean.setMac(Constants.MAC_TEST);
                subOrderReqBean.setTotal_amount(priceTotalTv.getText().toString());
                subOrderReqBean.setTotal_weight(weightTotalTv.getText().toString());
                subOrderReqBean.setCreate_time(getCurrentTime());
                subOrderReqBean.setGoods(goodsList);
                Bundle bundle = new Bundle();
                bundle.putSerializable("orderBean", subOrderReqBean);
                intent.putExtras(bundle);
                intent.setClass(this, UseCashActivity.class);
                startActivity(intent);

                break;
        }

    }

    public void getLoginInfo() {
        RetrofitFactory.getInstance().API()
                .getLoginInfo(AccountManager.getInstance().getToken(), Constants.MAC_TEST)
                .compose(this.<BaseEntity<LoginInfo>>setThread())
                .subscribe(new Observer<BaseEntity<LoginInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseEntity<LoginInfo> loginInfoBaseEntity) {
                        stallNumberTv.setText(loginInfoBaseEntity.getData().boothNumber);
                        operatorTv.setText(loginInfoBaseEntity.getData().name);
                        componyTitleTv.setText(loginInfoBaseEntity.getData().organizationName);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });

    }

    public void getGoodsData() {
        RetrofitFactory.getInstance().API()
                .getGoodsData(AccountManager.getInstance().getToken(), Constants.MAC_TEST)
                .compose(this.<BaseEntity<ScalesCategoryGoods>>setThread())
                .subscribe(new Observer<BaseEntity<ScalesCategoryGoods>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(BaseEntity<ScalesCategoryGoods> scalesCategoryGoodsBaseEntity) {
                        if (scalesCategoryGoodsBaseEntity.isSuccess()) {
                            hotKeyGoodsList.addAll(scalesCategoryGoodsBaseEntity.getData().hotKeyGoods);
                            gridAdapter.notifyDataSetChanged();
                        } else {
                            showLoading(scalesCategoryGoodsBaseEntity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });
    }

    class CommodityAdapter extends BaseAdapter {
        private Context context;
        private List<ScalesCategoryGoods.HotKeyGoods> list;

        public CommodityAdapter(Context context, List<ScalesCategoryGoods.HotKeyGoods> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.commodity_item, null);
                holder = new ViewHolder();
                holder.nameTv = convertView.findViewById(R.id.commodity_name_tv);
                holder.priceTv = convertView.findViewById(R.id.commodity_price_tv);
//                holder.countTv = convertView.findViewById(R.id.commodity_count_tv);
                holder.weightTv = convertView.findViewById(R.id.commodity_weight_tv);
                holder.subtotalTv = convertView.findViewById(R.id.commodity_subtotal_tv);
                holder.deleteBtn = convertView.findViewById(R.id.commodity_delete_btn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ScalesCategoryGoods.HotKeyGoods goods = list.get(position);
            holder.nameTv.setText(goods.name);
            holder.weightTv.setText(goods.weight);
            holder.priceTv.setText(goods.price);
            holder.subtotalTv.setText(goods.grandTotal);
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    seledtedGoodsList.remove(position);
                    commodityAdapter.notifyDataSetChanged();
                    return true;
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView nameTv;
            TextView priceTv;
            //            TextView countTv;
            TextView weightTv;
            TextView subtotalTv;
            Button deleteBtn;

        }
    }
}
