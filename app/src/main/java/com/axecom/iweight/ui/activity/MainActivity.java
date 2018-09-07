package com.axecom.iweight.ui.activity;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
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

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.base.BusEvent;
import com.axecom.iweight.bean.Advertis;
import com.axecom.iweight.bean.HotKeyBean;
import com.axecom.iweight.bean.HotKeyBean_Table;
import com.axecom.iweight.bean.LocalSettingsBean;
import com.axecom.iweight.bean.LoginInfo;
import com.axecom.iweight.bean.Order;
import com.axecom.iweight.bean.ScalesCategoryGoods;
import com.axecom.iweight.bean.SubOrderReqBean;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.manager.ClientManager;
import com.axecom.iweight.manager.MacManager;
import com.axecom.iweight.manager.PrinterManager;
import com.axecom.iweight.manager.ThreadPool;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.adapter.DigitalAdapter;
import com.axecom.iweight.ui.adapter.GridAdapter;
import com.axecom.iweight.ui.view.BTHelperDialog;
import com.axecom.iweight.utils.ButtonUtils;
import com.axecom.iweight.utils.LogUtils;
import com.axecom.iweight.utils.MoneyTextWatcher;
import com.axecom.iweight.utils.NetworkUtil;
import com.axecom.iweight.utils.SPUtils;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import top.wuhaojie.bthelper.BtHelperClient;

import static com.axecom.iweight.ui.activity.SystemSettingsActivity.KEY_PRICEING_METHOD;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String KEY_HOT_KEY_GOODS = "key_hot_key_goods";
    private static final String[] DATA_DIGITAL = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "删除", "0", "."};
    private View rootView;

    private GridView commoditysGridView;
    private GridView digitalGridView;
    private ListView commoditysListView;
    private CommodityAdapter commodityAdapter;
    private GridAdapter gridAdapter;
    private DigitalAdapter digitalAdapter;
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
    private TextView weightNumberTv;
    private TextView priceTotalTv;
    private TextView operatorTv;
    private TextView stallNumberTv;
    private TextView componyTitleTv;
    private TextView weightTopTv;
    private List<ScalesCategoryGoods.HotKeyGoods> hotKeyGoodsList;
    private List<ScalesCategoryGoods.HotKeyGoods> seledtedGoodsList;
    private ScalesCategoryGoods.HotKeyGoods selectedGoods;
    private int mTotalCopies = 1;
    private Bitmap bitmap;
    private String orderNo = "";
    private String payId = "";
    private boolean flag = true;
    private String deviceAddress;
    ThreadPoolExecutor executor;
    BTHelperDialog.Builder builder;
    private String port = "/dev/ttyS4";
    public BannerActivity banner = null;
    boolean switchSimpleOrComplex;
    boolean stopPrint;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);

        commoditysGridView = rootView.findViewById(R.id.main_commoditys_grid_view);
        digitalGridView = rootView.findViewById(R.id.main_digital_keys_grid_view);
        commoditysListView = rootView.findViewById(R.id.main_commoditys_list_view);
        weightLayout = rootView.findViewById(R.id.main_weight_layout);
        countLayout = rootView.findViewById(R.id.main_count_layout);
        countEt = rootView.findViewById(R.id.main_count_et);
        commodityNameTv = rootView.findViewById(R.id.main_commodity_name_tv);
        grandTotalTv = rootView.findViewById(R.id.main_grandtotal_tv);
        weightTotalTv = rootView.findViewById(R.id.main_weight_total_tv);
//        weightTotalMsgTv = rootView.findViewById(R.id.main_weight_total_msg_tv);
        weightTv = rootView.findViewById(R.id.main_weight_tv);
        operatorTv = rootView.findViewById(R.id.main_operator_tv);
        stallNumberTv = rootView.findViewById(R.id.main_stall_number_tv);
        weightNumberTv = rootView.findViewById(R.id.main_weight_number_tv);
        componyTitleTv = rootView.findViewById(R.id.main_compony_title_tv);
        priceTotalTv = rootView.findViewById(R.id.main_price_total_tv);
        weightTopTv = rootView.findViewById(R.id.main_weight_top_tv);
//        weightTopMsgTv = rootView.findViewById(R.id.main_weight_msg_tv);
        cashBtn = rootView.findViewById(R.id.main_cash_btn);
        settingsBtn = rootView.findViewById(R.id.main_settings_btn);
        mainClearBtn = rootView.findViewById(R.id.main_clear_btn);
        clearBtn = rootView.findViewById(R.id.main_digital_clear_btn);
        addBtn = rootView.findViewById(R.id.main_digital_add_btn);
        priceEt = rootView.findViewById(R.id.main_commodity_price_et);
        priceEt.requestFocus();
        priceEt.addTextChangedListener(new MoneyTextWatcher(priceEt));
        countEt.addTextChangedListener(countTextWatcher);
        disableShowInput(priceEt);
        disableShowInput(countEt);
        getLoginInfo();
        hotKeyGoodsList = new ArrayList<>();
        seledtedGoodsList = new ArrayList<>();
        gridAdapter = new GridAdapter(this, hotKeyGoodsList);


        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] presentationDisplays = displayManager.getDisplays();
        LogUtils.d("------------: " + presentationDisplays.length + "  --- " + presentationDisplays[1].getName());
        if (presentationDisplays.length > 1) {
            banner = new BannerActivity(this, presentationDisplays[1]);
        }
        banner.show();

        advertising();
        LocalSettingsBean.Value.PrinterPort printerPort = (LocalSettingsBean.Value.PrinterPort) SPUtils.readObject(this, LocalSettingsActivity.KEY_PRINTER_PORT);
        if (printerPort != null) {
            port = printerPort.val.split("：")[1];
        }
        if (TextUtils.equals(port, "/dev/ttyS4")) {
            printerManager.openGpinter();
        } else {
            printerManager.usbConn();
        }
        BlockingQueue workQueue = new LinkedBlockingDeque<>();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.DAYS, workQueue, threadFactory);

        mainClearBtn.setOnClickListener(this);
        cashBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        weightTopTv.setOnClickListener(this);
        return rootView;
    }

    PrinterManager printerManager = new PrinterManager(this);

    @Override
    public void initView() {
        weightNumberTv.setText(AccountManager.getInstance().getScalesId());

        commodityAdapter = new CommodityAdapter(this, seledtedGoodsList);
        commoditysListView.setAdapter(commodityAdapter);
        commoditysListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                seledtedGoodsList.remove(position);
                commodityAdapter.notifyDataSetChanged();
                calculatePrice();
                return true;
            }
        });

        commoditysGridView.setAdapter(gridAdapter);
        boolean isConnected = NetworkUtil.isConnected(this);
//        if (isConnected) {
            List<HotKeyBean> hotKeyBeanList = SQLite.select().from(HotKeyBean.class).queryList();
            if (hotKeyBeanList != null && hotKeyBeanList.size() > 0) {
                ScalesCategoryGoods.HotKeyGoods hotKeyGoods;
                for (HotKeyBean goods : hotKeyBeanList) {
                    hotKeyGoods = new ScalesCategoryGoods.HotKeyGoods();
                    hotKeyGoods.id = goods.id;
                    hotKeyGoods.cid = goods.cid;
                    hotKeyGoods.price = goods.price;
                    hotKeyGoods.weight = goods.weight;
                    hotKeyGoods.grandTotal = goods.grandTotal;
                    hotKeyGoods.traceable_code = goods.traceable_code;
                    hotKeyGoods.is_default = goods.is_default;
                    hotKeyGoods.name = goods.name;
                    hotKeyGoodsList.add(hotKeyGoods);
                }
                gridAdapter.notifyDataSetChanged();
            } else {
                getGoodsData();
            }

//        } else {
//            List<ScalesCategoryGoods.HotKeyGoods> saveHotKeys = (List<ScalesCategoryGoods.HotKeyGoods>) SPUtils.readObject(this, KEY_HOT_KEY_GOODS);
//            if (saveHotKeys != null) {
//                hotKeyGoodsList.addAll(saveHotKeys);
//                gridAdapter.notifyDataSetChanged();
//            }
//        }
        commoditysGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                flag = true;
////                executor.submit(new WeightThread());
//                selectedGoods = new ScalesCategoryGoods.HotKeyGoods();
//                selectedGoods.id = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).id;
//                selectedGoods.cid = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).cid;
//                selectedGoods.price = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).price;
//                selectedGoods.traceable_code = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).traceable_code;
//                selectedGoods.is_default = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).is_default;
//                selectedGoods.name = ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).name;
                selectedGoods = (ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position);
                commodityNameTv.setText(selectedGoods.name);
                priceEt.setText(selectedGoods.price);
                float count = 0;
                if (!TextUtils.isEmpty(countEt.getText())) {
                    count = Float.parseFloat(countEt.getText().toString());
                } else if (!TextUtils.isEmpty(countEt.getHint())) {
                    count = Float.parseFloat(countEt.getHint().toString());
                }
                if (switchSimpleOrComplex) {
                    grandTotalTv.setText(String.format("%.1f", Float.parseFloat(selectedGoods.price) * count));
                } else {
                    if (weightTopTv.getText().toString().indexOf('.') == -1 || weightTopTv.getText().length() - (weightTopTv.getText().toString().indexOf(".") + 1) <= 1) {
                        grandTotalTv.setText(String.format("%.1f", Float.parseFloat(selectedGoods.price) * Float.parseFloat(weightTopTv.getText().toString()) / 1000));
                    } else {
                        grandTotalTv.setText(String.format("%.1f", Float.parseFloat(selectedGoods.price) * Float.parseFloat(weightTopTv.getText().toString())));
                    }
                }

//                    if (Float.parseFloat(selectedGoods.price) > 0) {
//                        priceEt.setHint(selectedGoods.price);
//                        if (weightTopTv.getText().toString().indexOf('.') == -1 || weightTopTv.getText().length() - (weightTopTv.getText().toString().indexOf(".") + 1) <= 1) {
//                            grandTotalTv.setText(String.format("%.1f", Float.parseFloat(selectedGoods.price) * Float.parseFloat(weightTopTv.getText().toString()) / 1000));
//                        } else {
//                            grandTotalTv.setText(String.format("%.1f", Float.parseFloat(selectedGoods.price) * Float.parseFloat(weightTopTv.getText().toString())));
//                        }
//                    } else if (!TextUtils.isEmpty(priceEt.getText())) {
//                        if (weightTopTv.getText().toString().indexOf('.') == -1 || weightTopTv.getText().length() - (weightTopTv.getText().toString().indexOf(".") + 1) <= 1) {
//                            grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getText().toString()) * Float.parseFloat(weightTopTv.getText().toString()) / 1000));
//                        } else {
//                            grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getText().toString()) * Float.parseFloat(weightTopTv.getText().toString())));
//                        }
//                    } else if (!TextUtils.isEmpty(priceEt.getHint())) {
//                        if (weightTopTv.getText().toString().indexOf('.') == -1 || weightTopTv.getText().length() - (weightTopTv.getText().toString().indexOf(".") + 1) <= 1) {
//                            grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getHint().toString()) * Float.parseFloat(weightTopTv.getText().toString()) / 1000));
//                        } else {
//                            grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getHint().toString()) * Float.parseFloat(weightTopTv.getText().toString())));
//                        }
//                    }

                gridAdapter.setCheckedAtPosition(position);
                gridAdapter.notifyDataSetChanged();
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
                switch (rootView.findFocus().getId()) {
                    case R.id.main_count_et:
                        setEditText(countEt, position, text);
                        break;
                    case R.id.main_commodity_price_et:
                        setEditText(priceEt, position, text, 0);
                        break;
                }

                setGrandTotalTxt();
            }
        });

        if (!ClientManager.getClient().isBluetoothOpened()) {
            ClientManager.getClient().openBluetooth();
        }

        builder = new BTHelperDialog.Builder(this);
        deviceAddress = SPUtils.getString(this, BTHelperDialog.KEY_BT_ADDRESS, "");
        reConnect(deviceAddress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = true;
        LinkedHashMap valueMap = (LinkedHashMap) SPUtils.readObject(this, SystemSettingsActivity.KEY_STOP_PRINT);
        if (valueMap != null) {
            stopPrint = (boolean) valueMap.get("val");
        }
        switchSimpleOrComplex = (boolean) SPUtils.get(this, SettingsActivity.KET_SWITCH_SIMPLE_OR_COMPLEX, false);
        if (switchSimpleOrComplex) {
            countLayout.setVisibility(View.VISIBLE);
            weightLayout.setVisibility(View.GONE);
        } else {
            countEt.setText("0");
            countLayout.setVisibility(View.GONE);
            weightLayout.setVisibility(View.VISIBLE);
        }
        mTotalCopies = (int) SPUtils.get(this, LocalSettingsActivity.KEY_PRINTER_COUNT, mTotalCopies);

    }

    private TextWatcher countTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(".")) {
                    countEt.setText(s.subSequence(1, 2));
                    countEt.setSelection(1);
                    return;
                    
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            String temp = s.toString();
            int posDot = temp.indexOf(".");
            //小数点之前保留3位数字或者一千
            if (posDot <= 0){
                //temp
                if(temp.equals("10000")){
                    return;
                }else{
                    if(temp.length()<=4){
                        return;
                    }else{
                        s.delete(4, 5);
                        return;
                    }
                }
            }
            //保留三位小数
            if (temp.length() - posDot - 1 > 1)
            {
                s.delete(posDot + 2, posDot + 3);
            }

        }
    };

    public void advertising() {
        RetrofitFactory.getInstance().API()
                .advertising()
                .compose(this.<BaseEntity<Advertis>>setThread())
                .subscribe(new Observer<BaseEntity<Advertis>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseEntity<Advertis> advertisBaseEntity) {
                        if (advertisBaseEntity.isSuccess()) {
                            final Advertis advertis = advertisBaseEntity.getData();
                            List<String> list = new ArrayList<>();
                            for (int i = 0; i < advertis.list.size(); i++) {
                                list.add(advertis.list.get(i).img);
                            }
                            banner.convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                                @Override
                                public NetworkImageHolderView createHolder() {
                                    return new NetworkImageHolderView();
                                }
                            }, list).startTurning(2000);
                            banner.show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void reConnect(final String deviceAddress) {
//        if (!TextUtils.isEmpty(deviceAddress)) {
//            BtHelperClient btHelperClient = BtHelperClient.from(this);
//            btHelperClient.requestEnableBt();
//            btHelperClient.connectDevice(deviceAddress, new IErrorListener() {
//                @Override
//                public void onError(Exception e) {
//                    showLoading("蓝牙秤连接失败，请重试");
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onConnected(BtHelperClient.STATUS mCurrStatus) {
//                    SPUtils.putString(MainActivity.this, BTHelperDialog.KEY_BT_ADDRESS, deviceAddress);
//                    executor.submit(new WeightThread());
//                }
//
//            });
//        } else {
        builder.create(new BTHelperDialog.OnBtnClickListener() {

            @Override
            public void onConfirmed(BtHelperClient.STATUS mCurrStatus, String deviceAddress) {
                SPUtils.putString(MainActivity.this, BTHelperDialog.KEY_BT_ADDRESS, deviceAddress);
                executor.submit(new WeightThread());
            }

            @Override
            public void onCanceled(String result) {

            }
        }).show();
//        }

    }


    public class WeightThread extends Thread {
        BluetoothSocket socket;
        InputStream mInputStream;

        public WeightThread() {

        }

        public WeightThread(BluetoothSocket socket) {
            this.socket = socket;
            try {
                mInputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            mInputStream = BtHelperClient.from(MainActivity.this).mInputStream;
            if (mInputStream != null) {
                while (flag) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));
                    try {
                        String s = reader.readLine();
//                        LogUtils.d("-----------weight: " + s);
                        Message msg = Message.obtain();
                        msg.obj = s;
                        weightHandler.sendMessage(msg);
                        Thread.sleep(500);
//                        if (inputStream.available() == 0) {
//                            break;
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        reConnect(deviceAddress);
                        break;
                    }
                }
            }
        }
    }

    private Handler weightHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String weight = msg.obj.toString();
            if (!TextUtils.isEmpty(weight))
                if (weight.indexOf('.') == -1 || weight.length() - (weight.indexOf(".") + 1) <= 1) {
                    try {
                        float weightf = Float.parseFloat(msg.obj.toString().trim()) / 1000;
                        weightTv.setText(weightf + "kg");
                        weightTopTv.setText(weightf + "");
                        setGrandTotalTxt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    weightTv.setText(msg.obj.toString().trim() + "kg");
                    weightTopTv.setText(msg.obj.toString().trim());
                    setGrandTotalTxt();
                }
        }
    };

    public void setGrandTotalTxt() {
        float count = 0;
        if (!TextUtils.isEmpty(countEt.getText())) {
            count = Float.parseFloat(countEt.getText().toString());
        } else if (!TextUtils.isEmpty(countEt.getHint())) {
            count = Float.parseFloat(countEt.getHint().toString());
        }
        if (switchSimpleOrComplex) {
            if (!TextUtils.isEmpty(priceEt.getText())) {
                grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getText().toString()) * count));
            } else if (!TextUtils.isEmpty(priceEt.getHint())) {
                grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getHint().toString()) * count));
            }

        } else {
            if (!TextUtils.isEmpty(priceEt.getText())) {
                if (weightTopTv.getText().toString().indexOf('.') == -1 || weightTopTv.getText().length() - (weightTopTv.getText().toString().indexOf(".") + 1) <= 1) {
                    grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getText().toString()) * Float.parseFloat(weightTopTv.getText().toString()) / 1000));
                } else {
                    grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getText().toString()) * Float.parseFloat(weightTopTv.getText().toString())));
                }
            } else if (!TextUtils.isEmpty(priceEt.getHint())) {
                if (weightTopTv.getText().toString().indexOf('.') == -1 || weightTopTv.getText().length() - (weightTopTv.getText().toString().indexOf(".") + 1) <= 1) {
                    grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getHint().toString()) * Float.parseFloat(weightTopTv.getText().toString()) / 1000));
                } else {
                    try {
                        grandTotalTv.setText(String.format("%.1f", Float.parseFloat(priceEt.getHint().toString()) * Float.parseFloat(weightTopTv.getText().toString())));
                    } catch (Exception e) {

                    }
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            startDDMActivity(SettingsActivity.class, false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_cash_btn:
                if (!ButtonUtils.isFastDoubleClick(R.id.main_cash_btn)) {
                    if (Float.parseFloat(priceTotalTv.getText().toString()) > 0) {
                        showDialog(v);
                    }
                }
                break;
            case R.id.main_settings_btn:
                if (!ButtonUtils.isFastDoubleClick(R.id.home_card_number_tv)) {
                    Intent intent2 = new Intent();
                    intent2.setClass(this, StaffMemberLoginActivity.class);
                    startActivityForResult(intent2, 1002);
                }
                break;
            case R.id.main_clear_btn:
                clear(0);
                break;
            case R.id.main_digital_clear_btn:
                clear(1);
                break;
            case R.id.main_digital_add_btn:
                if (selectedGoods == null) {
                    return;
                }
                if (TextUtils.isEmpty(priceEt.getText()) && TextUtils.isEmpty(priceEt.getHint().toString())) {
                    return;
                }
                if (Float.parseFloat(grandTotalTv.getText().toString()) <= 0) {
                    return;
                }
                selectedGoods.weight = weightTopTv.getText().toString();
                selectedGoods.price = TextUtils.isEmpty(priceEt.getText().toString()) ? priceEt.getHint().toString() : priceEt.getText().toString();
                selectedGoods.grandTotal = grandTotalTv.getText().toString();
                selectedGoods.count = countEt.getText().toString();
                seledtedGoodsList.add(selectedGoods);
                commodityAdapter.notifyDataSetChanged();
//                HotKeyBean hotKeyBean = new HotKeyBean();
//                hotKeyBean.id = selectedGoods.id;
//                hotKeyBean.cid = selectedGoods.cid;
//                hotKeyBean.grandTotal = selectedGoods.grandTotal;
//                hotKeyBean.is_default = selectedGoods.is_default;
//                hotKeyBean.name = selectedGoods.name;
//                hotKeyBean.local_price = selectedGoods.price;
//                hotKeyBean.traceable_code = selectedGoods.traceable_code;
//                hotKeyBean.weight = selectedGoods.weight;
                SQLite.update(HotKeyBean.class)
                        .set(HotKeyBean_Table.price.eq(selectedGoods.price))
                        .where(HotKeyBean_Table.id.eq(selectedGoods.id))
                        .query();
                calculatePrice();
                clear(3);
                break;
        }
    }

    public void calculatePrice() {
        float weightTotalF = 0.0000f;
        float priceTotal = 0;
        for (int i = 0; i < seledtedGoodsList.size(); i++) {
            ScalesCategoryGoods.HotKeyGoods goods = seledtedGoodsList.get(i);
            if (goods.price != null) {
                if (!TextUtils.isEmpty(goods.price)) {
//                    if ((goods.weight).indexOf('.') == -1 || goods.weight.length() - (goods.weight.indexOf(".") + 1) <= 1) {
                    weightTotalF += Float.parseFloat(goods.weight);
//                    } else {
//                        weightTotalF += Float.parseFloat(goods.weight);
//                    }
                    priceTotal += Float.parseFloat(goods.grandTotal);
                }
            }
        }

        weightTotalTv.setText(String.format("%.2f", weightTotalF));
        priceTotalTv.setText(String.format("%.2f", priceTotal));
//        priceTotalTv.setText(String.format("%.2f", priceTotal));
    }


    @Override
    public void onEventMainThread(BusEvent event) {
        if (event != null) {
            if (event.getType() == BusEvent.PRINTER_LABEL || event.getType() == BusEvent.POSITION_PATCH) {
                if(event.getType() == BusEvent.PRINTER_LABEL){
                    showLoading("支付成功");
                }

                final String qrcode = event.getmStrParam03();
                bitmap = (Bitmap) event.getParam();
                if (bitmap == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String bmpUrl = SPUtils.getString(MainActivity.this, "print_bitmap", "");
                                String price = SPUtils.getString(MainActivity.this, "print_price", "");
                                String orderNo = SPUtils.getString(MainActivity.this, "print_orderno", "");
                                String payId = SPUtils.getString(MainActivity.this, "print_payid", "");
                                String priceTotal = SPUtils.getString(MainActivity.this, "print_price", "");
                                bitmap = BitmapFactory.decodeStream(new URL(bmpUrl).openStream());
                                if (TextUtils.equals(port, "/dev/ttyS4")) {
                                    btnReceiptPrint(bitmap,
                                            orderNo,
                                            payId,
                                            priceTotal,
                                            price,
                                            (List<ScalesCategoryGoods.HotKeyGoods>) SPUtils.readObject(MainActivity.this, "selectedGoodList"));
                                } else {
                                    btnLabelPrint(bitmap,
                                            orderNo,
                                            payId,
                                            priceTotal,
                                            price,
                                            (List<ScalesCategoryGoods.HotKeyGoods>) SPUtils.readObject(MainActivity.this, "selectedGoodList"));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    if (stopPrint) {
                        return;
                    }
                    orderNo = event.getStrParam();
                    payId = event.getStrParam02();
                    SPUtils.putString(this, "print_orderno", orderNo);
                    SPUtils.putString(this, "print_payid", payId);
                    SPUtils.putString(this, "print_price", priceTotalTv.getText().toString());
                    if (TextUtils.equals(port, "/dev/ttyS4")) {
                        btnReceiptPrint(bitmap,
                                orderNo,
                                payId,
                                operatorTv.getText().toString(),
                                priceTotalTv.getText().toString(),
                                (List<ScalesCategoryGoods.HotKeyGoods>) SPUtils.readObject(this, "selectedGoodList"));
                    } else {
                        btnLabelPrint(bitmap,
                                orderNo,
                                payId,
                                operatorTv.getText().toString(),
                                priceTotalTv.getText().toString(),
                                (List<ScalesCategoryGoods.HotKeyGoods>) SPUtils.readObject(this, "selectedGoodList"));
                    }
                }

                clear(1);
            }
            if (event.getType() == BusEvent.PRINTER_NO_BITMAP) {
                showLoading("支付成功");
                orderNo = (Math.random() * 9 + 1) * 100000 + getCurrentTime("yyyyMMddHHmmss");
                payId = event.getStrParam02();
                SPUtils.putString(this, "print_price", priceTotalTv.getText().toString());
                if (TextUtils.equals(port, "/dev/ttyS4")) {
                    btnReceiptPrint(bitmap,
                            orderNo,
                            payId,
                            operatorTv.getText().toString(),
                            priceTotalTv.getText().toString(),
                            (List<ScalesCategoryGoods.HotKeyGoods>) SPUtils.readObject(this, "selectedGoodList"));
                } else {
                    btnLabelPrint(bitmap,
                            orderNo,
                            payId,
                            operatorTv.getText().toString(),
                            priceTotalTv.getText().toString(),
                            (List<ScalesCategoryGoods.HotKeyGoods>) SPUtils.readObject(this, "selectedGoodList"));
                }
                clear(1);
            }
            if (event.getType() == BusEvent.NET_WORK_AVAILABLE) {
                boolean available = event.getBooleanParam();
                if (available) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isAvailable()) {
                        List<SubOrderReqBean> orders = (List<SubOrderReqBean>) SPUtils.readObject(MainActivity.this, "local_order");
                        submitOrders(orders);
//                getGoodsData();
//                getLoginInfo();
                    }
//                    else {
//                        SPUtils.saveObject(MainActivity.this, KEY_HOT_KEY_GOODS, hotKeyGoodsList);
//                    }
                }
            }
            if (event.getType() == BusEvent.SAVE_COMMODITY_SUCCESS) {
//                SPUtils.saveObject(MainActivity.this, KEY_HOT_KEY_GOODS, hotKeyGoodsList);
//                boolean isConnected = NetworkUtil.isConnected(this);
//                if (isConnected) {
                    getGoodsData();


//                } else {
//                    List<ScalesCategoryGoods.HotKeyGoods> saveHotKeys = (List<ScalesCategoryGoods.HotKeyGoods>) SPUtils.readObject(this, KEY_HOT_KEY_GOODS);
//                    if (saveHotKeys != null) {
//                        hotKeyGoodsList.addAll(saveHotKeys);
//                        gridAdapter.notifyDataSetChanged();
//                    }
//                }
//                else {
//                    if (saveHotKeys != null) {
//                        hotKeyGoodsList.addAll(saveHotKeys);
//                        gridAdapter.notifyDataSetChanged();
//                    }
//                }
            }
            if (event.getType() == BusEvent.SAVE_LOCAL_SUCCESS) {
                LocalSettingsBean.Value.PrinterPort printerPort = (LocalSettingsBean.Value.PrinterPort) SPUtils.readObject(this, LocalSettingsActivity.KEY_PRINTER_PORT);
                if (printerPort != null) {
                    port = printerPort.val.split("：")[1];
                }
                if (TextUtils.equals(port, "/dev/ttyS4")) {
                    printerManager.openGpinter();
                } else {
                    printerManager.usbConn();
                }
            }
            if (event.getType() == BusEvent.BLUETOOTH_CONNECTED) {
                if (event.getBooleanParam()) {
                    executor.submit(new WeightThread());
                }
            }
        }

    }

    private ThreadPool threadPool;

    public void btnReceiptPrint(final Bitmap bitmap, final String orderNo, final String payId, final String operator, final String price, final List<ScalesCategoryGoods.HotKeyGoods> seledtedGoodsList) {
        threadPool = ThreadPool.getInstantiation();
        threadPool.addTask(new Runnable() {
            @Override
            public void run() {
                printerManager.printer(orderNo, payId, operator, price, bitmap, seledtedGoodsList);
            }
        });
    }

    public void btnLabelPrint(final Bitmap bitmap, final String orderNo, final String payId, final String operator, final String price, final List<ScalesCategoryGoods.HotKeyGoods> seledtedGoodsList) {
        threadPool = ThreadPool.getInstantiation();
        threadPool.addTask(new Runnable() {
            @Override
            public void run() {
                printerManager.sendLabel(orderNo, payId, operator, price, bitmap, seledtedGoodsList);
            }
        });
    }

    public void clear(int type) {
        if (type == 0) {
            selectedGoods = null;
            commodityNameTv.setText("");
            priceEt.setText("");
            grandTotalTv.setText("0.0");
        }
        if (type == 1) {
            weightTopTv.setText("0.000");
            weightTv.setText("");
            weightTotalTv.setText("0");
            grandTotalTv.setText("0.0");
            priceTotalTv.setText("0.00");
            seledtedGoodsList.clear();
            commodityAdapter = new CommodityAdapter(this, seledtedGoodsList);
            commoditysListView.setAdapter(commodityAdapter);
        }
        if (type == 3) {
            selectedGoods = null;
            commodityNameTv.setText("");
            String hint = "";
            if (!TextUtils.isEmpty(priceEt.getText())) {
                hint = priceEt.getText().toString();
            } else if (!TextUtils.isEmpty(priceEt.getHint())) {
                hint = priceEt.getHint().toString();
            }
            priceEt.setText("");
            priceEt.setHint(hint);
        }
    }

    public void showDialog(View v) {
        switch (v.getId()) {
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
                    good.setGoods_number(countEt.getText().toString());
                    good.setGoods_weight(hotKeyGoods.weight);
                    good.setGoods_amount(hotKeyGoods.grandTotal);
                    goodsList.add(good);
                }
                subOrderReqBean.setToken(AccountManager.getInstance().getToken());
                subOrderReqBean.setMac(MacManager.getInstace(this).getMac());
                subOrderReqBean.setTotal_amount(priceTotalTv.getText().toString());
                subOrderReqBean.setTotal_weight(weightTotalTv.getText().toString());
                subOrderReqBean.setCreate_time(getCurrentTime());
                subOrderReqBean.setGoods(goodsList);
                if (switchSimpleOrComplex) {
                    subOrderReqBean.setPricing_model("2");
                }else {
                    subOrderReqBean.setPricing_model("1");
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("orderBean", subOrderReqBean);
                intent.putExtras(bundle);
                intent.setClass(this, UseCashActivity.class);
                startActivity(intent);
                SPUtils.saveObject(this, "selectedGoodList", seledtedGoodsList);
                break;
        }

    }

    public void getLoginInfo() {
        RetrofitFactory.getInstance().API()
                .getLoginInfo(AccountManager.getInstance().getToken(), MacManager.getInstace(this).getMac())
                .compose(this.<BaseEntity<LoginInfo>>setThread())
                .subscribe(new Observer<BaseEntity<LoginInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseEntity<LoginInfo> loginInfoBaseEntity) {
                        if (loginInfoBaseEntity.isSuccess()) {
                            stallNumberTv.setText(loginInfoBaseEntity.getData().boothNumber);
                            operatorTv.setText(loginInfoBaseEntity.getData().name);
                            componyTitleTv.setText(loginInfoBaseEntity.getData().organizationName);
                        }
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
                .getGoodsData(AccountManager.getInstance().getToken(), MacManager.getInstace(this).getMac())
//                .getGoodsData(AccountManager.getInstance().getToken(), "80:5e:4f:85:57:9d")
                .compose(this.<BaseEntity<ScalesCategoryGoods>>setThread())
                .subscribe(new Observer<BaseEntity<ScalesCategoryGoods>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseEntity<ScalesCategoryGoods> scalesCategoryGoodsBaseEntity) {
                        if (scalesCategoryGoodsBaseEntity.isSuccess()) {
                            ScalesCategoryGoods hotKeyGoods = scalesCategoryGoodsBaseEntity.getData();
                            hotKeyGoodsList.clear();
                            hotKeyGoodsList.addAll(scalesCategoryGoodsBaseEntity.getData().hotKeyGoods);
                            HotKeyBean hotKey = new HotKeyBean();
                            ModelAdapter<HotKeyBean> modelAdapter = FlowManager.getModelAdapter(HotKeyBean.class);
                            for (ScalesCategoryGoods.HotKeyGoods goods : hotKeyGoodsList) {
                                hotKey.id = goods.id;
                                hotKey.cid = goods.cid;
                                hotKey.grandTotal = goods.grandTotal;
                                hotKey.is_default = goods.is_default;
                                hotKey.name = goods.name;
                                hotKey.price = goods.price;
                                hotKey.traceable_code = goods.traceable_code;
                                hotKey.weight = goods.weight;
                                modelAdapter.insert(hotKey);
                            }
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
            if (Integer.parseInt(goods.count) > 0) {
                holder.weightTv.setText(goods.count);
            } else {
                if ((goods.weight).indexOf('.') == -1 || goods.weight.length() - (goods.weight.indexOf(".") + 1) <= 1) {
                    holder.weightTv.setText(getString(R.string.string_weight_unit_kg, Float.parseFloat(goods.weight) / 1000 + ""));
                } else {
                    holder.weightTv.setText(getString(R.string.string_weight_unit_kg, goods.weight));
                }
            }
            holder.priceTv.setText(goods.price);
            holder.subtotalTv.setText(goods.grandTotal);
//            convertView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                }
//            });

            return convertView;
        }

        class ViewHolder {
            TextView nameTv;
            TextView priceTv;
            TextView weightTv;
            TextView subtotalTv;
            Button deleteBtn;

        }
    }
}
