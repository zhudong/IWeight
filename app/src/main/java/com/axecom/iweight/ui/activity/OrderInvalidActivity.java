package com.axecom.iweight.ui.activity;

import android.app.AlertDialog;
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
 * Created by Administrator on 2018-5-22.
 */

public class OrderInvalidActivity extends BaseActivity {

    private View rootView;
    private ListView orderListView;
    private OrderAdapter orderAdapter;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.order_invalid_activity, null);
        orderListView = rootView.findViewById(R.id.order_invalid_listview);
        return rootView;
    }

    @Override
    public void initView() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("11111111111111111111");
        }
        orderAdapter = new OrderAdapter(this, list);
        orderListView.setAdapter(orderAdapter);
    }

    @Override
    public void onClick(View v) {

    }

    class OrderAdapter extends BaseAdapter{

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
                convertView = LayoutInflater.from(context).inflate(R.layout.order_item, null);
                holder = new ViewHolder();
                holder.orderNumberTv = convertView.findViewById(R.id.order_item_number_tv);
                holder.dealTimeTv = convertView.findViewById(R.id.order_item_dealTime_tv);
                holder.sellerTv = convertView.findViewById(R.id.order_item_seller_tv);
                holder.buyerTv = convertView.findViewById(R.id.order_item_buyer_tv);
                holder.weightTv = convertView.findViewById(R.id.order_item_weight_tv);
                holder.incomeTv = convertView.findViewById(R.id.order_item_income_tv);
                holder.billingTv = convertView.findViewById(R.id.order_item_billing_tv);
                holder.orderStatusTv = convertView.findViewById(R.id.order_item_status_tv);
                holder.invalidBtn = convertView.findViewById(R.id.order_item_invalid_btn);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.orderNumberTv.setText(list.get(position));
            holder.invalidBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.dialog);
                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
                    builder.setView(view);
                    Button confirmBtn = view.findViewById(R.id.dialog_confirm_btn);
                    Button cancelBtn = view.findViewById(R.id.dialog_cancel_btn);
                    final AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
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
            Button invalidBtn;
        }
    }
}
