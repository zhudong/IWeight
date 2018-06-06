package com.axecom.iweight.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.manager.ClientManager;
import com.axecom.iweight.manager.GPprinterManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.adapter.DigitalAdapter;
import com.axecom.iweight.ui.adapter.GridAdapter;
import com.axecom.iweight.ui.view.BTHelperDialog;
import com.axecom.iweight.ui.view.BluetoothDialog;
import com.axecom.iweight.utils.LogUtils;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.jb.sdk.command.ReceiptCommand;
import com.jb.sdk.service.JbPrintService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.OnSearchDeviceListener;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] DATA_DIGITAL = {"1","2","3","4","5","6","7","8","9","删除","0","."};

    private View rootView;

    private GridView commoditysGridView;
    private GridView digitalGridView;
    private ListView commoditysListView;
    private CommodityAdapter commodityAdapter;
    private GridAdapter gridAdapter;
    private DigitalAdapter digitalAdapter;
    private Button bankCardBtn;
    private Button cashBtn;
    private Button settingsBtn;
    private GPprinterManager gPprinterManager;


    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);

        commoditysGridView = rootView.findViewById(R.id.main_commoditys_grid_view);
        digitalGridView = rootView.findViewById(R.id.main_digital_keys_grid_view);
        commoditysListView = rootView.findViewById(R.id.main_commoditys_list_view);
//        bankCardBtn = rootView.findViewById(R.id.main_bank_card_btn);
        cashBtn = rootView.findViewById(R.id.main_cash_btn);
        settingsBtn = rootView.findViewById(R.id.main_settings_btn);


        gPprinterManager = new GPprinterManager(this);
//        gPprinterManager.openConnect();
//        bankCardBtn.setOnClickListener(this);
        cashBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("白菜11111111111" + i);
        }
        commodityAdapter = new CommodityAdapter(this, list);
        commoditysListView.setAdapter(commodityAdapter);

        List<String> list2 = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            list2.add("白菜111111111" + i);
        }
        list2.add("上翻");
        list2.add("下翻");
        gridAdapter = new GridAdapter(this, list2);
        commoditysGridView.setAdapter(gridAdapter);

        List<String> digitaList = new ArrayList<>();

        for (int i = 0; i < DATA_DIGITAL.length; i++) {
            digitaList.add(DATA_DIGITAL[i]);
        }
        digitalAdapter = new DigitalAdapter(this, digitaList);
        digitalGridView.setAdapter(digitalAdapter);


        if(!ClientManager.getClient().isBluetoothOpened()){
            ClientManager.getClient().openBluetooth();
        }


        BTHelperDialog.Builder builder = new BTHelperDialog.Builder(this);
        builder.create(new BTHelperDialog.OnBtnClickListener() {
            @Override
            public void onConfirmed(String result) {

            }

            @Override
            public void onCanceled(String result) {

            }
        }).show();


        test();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        gPprinterManager.closeConnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.main_bank_card_btn:
            case R.id.main_cash_btn:
                startDDMActivity(SettingsActivity.class, false);
//                showDialog(v);
                break;
            case R.id.main_settings_btn:
                startDDMActivity(SettingsActivity.class, false);
                break;
            case R.id.main_clear_btn:
                gPprinterManager.openConnect();
                gPprinterManager.printTest();
                break;
        }
    }

    public void showDialog(View v){
        Intent intent = new Intent();
        switch (v.getId()){
//            case R.id.main_bank_card_btn:
//                intent.setClass(this, UseBankCardActivity.class);
//                break;
            case R.id.main_cash_btn:
                intent.setClass(this, UseCashActivity.class);
                break;
        }
        startActivity(intent);

    }


    public void test(){
       RetrofitFactory.getInstance().API()
               .test()
               .compose(this.<String>setThread())
               .subscribe(new Observer<String>() {
                   @Override
                   public void onSubscribe(@NonNull Disposable d) {

                   }

                   @Override
                   public void onNext(@NonNull String s) {
                       LogUtils.d(s);
                   }

                   @Override
                   public void onError(@NonNull Throwable e) {

                   }

                   @Override
                   public void onComplete() {

                   }
               });
    }

    class CommodityAdapter extends BaseAdapter{
        private Context context;
        private List<String> list;

        public CommodityAdapter(Context context, List<String> list){
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.commodity_item, null);
                holder = new ViewHolder();
                holder.nameTv = convertView.findViewById(R.id.commodity_name_tv);
                holder.priceTv = convertView.findViewById(R.id.commodity_price_tv);
//                holder.countTv = convertView.findViewById(R.id.commodity_count_tv);
                holder.weightTv = convertView.findViewById(R.id.commodity_weight_tv);
                holder.subtotalTv = convertView.findViewById(R.id.commodity_subtotal_tv);
                holder.deleteBtn = convertView.findViewById(R.id.commodity_delete_btn);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.nameTv.setText(list.get(position));
            return convertView;
        }

        class ViewHolder{
            TextView nameTv;
            TextView priceTv;
//            TextView countTv;
            TextView weightTv;
            TextView subtotalTv;
            Button deleteBtn;

        }
    }
}
