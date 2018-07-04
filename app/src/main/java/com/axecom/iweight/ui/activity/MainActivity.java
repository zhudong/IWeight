package com.axecom.iweight.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.OnSearchDeviceListener;

import static com.axecom.iweight.base.SysApplication.mContext;
import static com.nostra13.universalimageloader.utils.IoUtils.DEFAULT_BUFFER_SIZE;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] DATA_DIGITAL = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "删除", "0", "."};

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
    private GPprinterManager gPprinterManager;
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
    private List<ScalesCategoryGoods.HotKeyGoods> hotKeyGoodsList;
    private List<ScalesCategoryGoods.HotKeyGoods> seledtedGoodsList;
    private ScalesCategoryGoods.HotKeyGoods selectedGoods;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);

        commoditysGridView = rootView.findViewById(R.id.main_commoditys_grid_view);
        digitalGridView = rootView.findViewById(R.id.main_digital_keys_grid_view);
        commoditysListView = rootView.findViewById(R.id.main_commoditys_list_view);
//        bankCardBtn = rootView.findViewById(R.id.main_bank_card_btn);
        commodityNameTv = rootView.findViewById(R.id.main_commodity_name_tv);
        grandTotalTv = rootView.findViewById(R.id.main_grandtotal_tv);
        weightTotalTv = rootView.findViewById(R.id.main_weight_total_tv);
        weightTv = rootView.findViewById(R.id.main_weight_tv);
        operatorTv = rootView.findViewById(R.id.main_operator_tv);
        stallNumberTv = rootView.findViewById(R.id.main_stall_number_tv);
        componyTitleTv = rootView.findViewById(R.id.main_compony_title_tv);
        priceTotalTv = rootView.findViewById(R.id.main_price_total_tv);
        cashBtn = rootView.findViewById(R.id.main_cash_btn);
        settingsBtn = rootView.findViewById(R.id.main_settings_btn);
        mainClearBtn = rootView.findViewById(R.id.main_clear_btn);
        clearBtn = rootView.findViewById(R.id.main_digital_clear_btn);
        addBtn = rootView.findViewById(R.id.main_digital_add_btn);
        priceEt = rootView.findViewById(R.id.main_commodity_price_et);
        priceEt.requestFocus();
        disableShowInput(priceEt);

        gPprinterManager = new GPprinterManager(this);
//        gPprinterManager.openConnect();
//        bankCardBtn.setOnClickListener(this);
        mainClearBtn.setOnClickListener(this);
        cashBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
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
                selectedGoods.id =  ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).id;
                selectedGoods.cid =  ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).cid;
                selectedGoods.price =  ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).price;
                selectedGoods.traceable_code =  ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).traceable_code;
                selectedGoods.is_default =  ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).is_default;
                selectedGoods.name =  ((ScalesCategoryGoods.HotKeyGoods) parent.getAdapter().getItem(position)).name;
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
                if(selectedGoods == null){
                    return;
                }
                String text = parent.getAdapter().getItem(position).toString();
                setEditText(priceEt, position, text);
                grandTotalTv.setText(priceEt.getText().toString() + " 元");
                selectedGoods.price = priceEt.getText().toString();
                selectedGoods.grandTotal = priceEt.getText().toString();
            }
        });
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
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
        gPprinterManager.closeConnect();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.main_bank_card_btn:
            case R.id.main_cash_btn:
                startDDMActivity(SettingsActivity.class, false);
//                showDialog(v);
                break;
            case R.id.main_settings_btn:
                startDDMActivity(StaffMemberLoginActivity.class, false);
                break;
            case R.id.main_clear_btn:
                gPprinterManager.openConnect();
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
//                submitOrder(subOrderReqBean);
                break;
            case R.id.main_digital_clear_btn:
                gPprinterManager.printTestPaper();
                priceEt.setText("");
                grandTotalTv.setText("");
                break;
            case R.id.main_digital_add_btn:
                if(selectedGoods == null){
                    return;
                }
                seledtedGoodsList.add(selectedGoods);
                commodityAdapter.notifyDataSetChanged();
                int weightTotal = 0;
                int priceTotal = 0;
                for (int i = 0; i < seledtedGoodsList.size(); i++) {
                    ScalesCategoryGoods.HotKeyGoods goods = seledtedGoodsList.get(i);
//                    weightTotal+=Integer.parseInt(goods.weight);
                    if (goods.price!= null) {
                        if(!TextUtils.isEmpty(goods.price)){
                            priceTotal += Integer.parseInt(goods.price);
                        }
                    }
                }
                weightTotalTv.setText(weightTotal + "");
                priceTotalTv.setText(priceTotal + "");
                clear();
                break;
        }
    }

    public void clear(){
        selectedGoods = null;
        grandTotalTv.setText("0.00");
        commodityNameTv.setText("");
        priceEt.setText("");
        weightTv.setText("");
    }

    public void showDialog(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
//            case R.id.main_bank_card_btn:
//                intent.setClass(this, UseBankCardActivity.class);
//                break;
            case R.id.main_cash_btn:
                intent.setClass(this, UseCashActivity.class);
                break;
        }
        startActivity(intent);

    }

    public void getLoginInfo(){
        RetrofitFactory.getInstance().API()
                .getLoginInfo(AccountManager.getInstance().getToken(), Constants.MAC_TEST)
                .compose(this.<BaseEntity<LoginInfo>>setThread())
                .subscribe(new Observer<BaseEntity<LoginInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
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
