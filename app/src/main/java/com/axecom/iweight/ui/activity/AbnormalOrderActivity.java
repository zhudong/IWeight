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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-5-25.
 */

public class AbnormalOrderActivity extends BaseActivity {

    private View rootView;

    private ListView orderListView;
    private OrderAdapter orderAdapter;
    private Button previousBtn, nextBtn, backBtn;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.abnormal_orders_activity, null);

        orderListView = rootView.findViewById(R.id.abnormal_order_listview);
        previousBtn = rootView.findViewById(R.id.abnormal_order_previous_btn);
        nextBtn = rootView.findViewById(R.id.abnormal_order_next_btn);
        backBtn = rootView.findViewById(R.id.abnormal_order_back_btn);

        previousBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("ddddddddddddd" + i);
        }
        orderAdapter = new OrderAdapter(this, list);
        orderListView.setAdapter(orderAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.abnormal_order_back_btn:
                finish();
                break;
        }
    }

    class OrderAdapter extends BaseAdapter {

        private Context context;
        private List<String> list;

        public OrderAdapter(Context context, List<String> list){
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
            ViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.abnormal_order_item, null);
                holder = new ViewHolder();
                holder.orderNumberTv = convertView.findViewById(R.id.abnormal_order_item_number_tv);
                holder.dealTimeTv = convertView.findViewById(R.id.abnormal_order_item_dealTime_tv);
                holder.sellerTv = convertView.findViewById(R.id.abnormal_order_item_seller_tv);
                holder.buyerTv = convertView.findViewById(R.id.abnormal_order_item_buyer_tv);
                holder.weightTv = convertView.findViewById(R.id.abnormal_order_item_weight_tv);
                holder.incomeTv = convertView.findViewById(R.id.abnormal_order_item_income_tv);
                holder.billingTv = convertView.findViewById(R.id.abnormal_order_item_billing_tv);
                holder.orderStatusTv = convertView.findViewById(R.id.abnormal_order_item_status_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.orderNumberTv.setText(list.get(position));

            return convertView;
        }

        class ViewHolder{
            TextView orderNumberTv;
            TextView dealTimeTv;
            TextView sellerTv;
            TextView buyerTv;
            TextView weightTv;
            TextView incomeTv;
            TextView billingTv;
            TextView orderStatusTv;
        }
    }
}
