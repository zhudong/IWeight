package com.axecom.iweight.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity {

    private static final String[] DATA_DIGITAL = {"7","8","9","4","5","6","1","2","3","删除","0","."};

    private View rootView;

    private GridView commoditysGridView;
    private GridView digitalGridView;
    private ListView commoditysListView;
    private CommodityAdapter commodityAdapter;
    private GridAdapter gridAdapter;
    private DigitalAdapter digitalAdapter;
    private Button bankCardBtn;
    private Button cashBtn;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);

        commoditysGridView = rootView.findViewById(R.id.main_commoditys_grid_view);
        digitalGridView = rootView.findViewById(R.id.main_digital_keys_grid_view);
        commoditysListView = rootView.findViewById(R.id.main_commoditys_list_view);
        bankCardBtn = rootView.findViewById(R.id.main_bank_card_btn);
        cashBtn = rootView.findViewById(R.id.main_cash_btn);

        bankCardBtn.setOnClickListener(this);
        cashBtn.setOnClickListener(this);
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

        test();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_bank_card_btn:
            case R.id.main_cash_btn:
                showDialog(v);
                break;
        }
    }

    public void showDialog(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.main_bank_card_btn:
                intent.setClass(this, UseBankCardActivity.class);
                break;
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

    class DigitalAdapter extends BaseAdapter{

        private Context context;
        private List<String> list;

        public DigitalAdapter(Context context, List<String> list){
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
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.digital_item, null);
                holder = new ViewHolder();
                holder.digitalBtn = convertView.findViewById(R.id.main_digitial_btn);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.digitalBtn.setText(list.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return convertView;
        }
        class ViewHolder{
            Button digitalBtn;
        }
    }

    class GridAdapter extends BaseAdapter{
        private Context context;
        private List<String> list;

        public GridAdapter(Context context, List<String> list){
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
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.main_grid_item, null);
                holder = new ViewHolder();
                holder.commodityBtn = convertView.findViewById(R.id.main_grid_item_btn);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.commodityBtn.setText(list.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position == list.size() - 1){
                        commoditysGridView.smoothScrollToPosition(5);
                    }
                    if(position == list.size() - 2){
                        commoditysGridView.smoothScrollToPosition(1);
                    }
                }
            });

            return convertView;
        }

        class ViewHolder{
            Button commodityBtn;
        }
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
