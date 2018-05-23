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
import com.axecom.iweight.ui.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-5-22.
 */

public class OrderInvalidActivity extends BaseActivity {

    private View rootView;
    private ListView orderListView;
    private OrderAdapter orderAdapter;
    private Button previousBtn, nextBtn, backBtn;
    private int previousPos = 8;
    private int nextPos = 16;
    private  CustomDialog mDialog;
    private CustomDialog.Builder builder;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.order_invalid_activity, null);
        orderListView = rootView.findViewById(R.id.order_invalid_listview);
        previousBtn = rootView.findViewById(R.id.order_invalid_previous_btn);
        nextBtn = rootView.findViewById(R.id.order_invalid_next_btn);
        backBtn = rootView.findViewById(R.id.order_invalid_back_btn);
        builder = new CustomDialog.Builder(this);

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
            case R.id.order_invalid_previous_btn:
                scrollTo(orderListView.getFirstVisiblePosition() - previousPos <= 0 ? 0 : orderListView.getFirstVisiblePosition() - previousPos);
                break;
            case R.id.order_invalid_next_btn:
                scrollTo(orderListView.getFirstVisiblePosition() + nextPos);
                break;
            case R.id.order_invalid_back_btn:
                finish();
                break;
        }
    }

    public void scrollTo(final int position){
        orderListView.post(new Runnable() {
            @Override
            public void run() {
                orderListView.smoothScrollToPosition(position);
            }
        });
    }

    private void showTwoButtonDialog(String alertText, String confirmText, String cancelText, View.OnClickListener conFirmListener, View.OnClickListener cancelListener) {
        mDialog = builder.setMessage(alertText)
                .setPositiveButton(confirmText, conFirmListener)
                .setNegativeButton(cancelText, cancelListener)
                .createTwoButtonDialog();
        mDialog.show();
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
                    showTwoButtonDialog(getString(R.string.string_confirm_invalid_txt),
                            getString(R.string.string_confirm),
                            getString(R.string.string_cancel_txt),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();

                                }
                            });

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
