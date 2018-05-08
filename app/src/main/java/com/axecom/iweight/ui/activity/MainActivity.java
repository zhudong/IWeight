package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

    private View rootView;

    private ListView commoditysListView;
    private CommodityAdapter commodityAdapter;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        commoditysListView = rootView.findViewById(R.id.main_commoditys_list_view);

        return rootView;
    }

    @Override
    public void initView() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("白菜" + i);
        }
        commodityAdapter = new CommodityAdapter(this, list);
        commoditysListView.setAdapter(commodityAdapter);
        test();
    }

    @Override
    public void onClick(View v) {

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
                holder.countTv = convertView.findViewById(R.id.commodity_count_tv);
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
            TextView countTv;
            TextView weightTv;
            TextView subtotalTv;
            Button deleteBtn;

        }
    }
}
