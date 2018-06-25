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
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.UnusualOrdersBean;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.net.RetrofitFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018-5-25.
 */

public class AbnormalOrderActivity extends BaseActivity {

    private View rootView;

    private ListView orderListView;
    private OrderAdapter orderAdapter;
    private Button previousBtn, nextBtn, backBtn;
    private List<UnusualOrdersBean.Order> orderList;

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
        getOrders("1", "10", "2");
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        orderListView.setAdapter(orderAdapter);
    }

    public void getOrders(String page, String pageNum, String typeVal){
        RetrofitFactory.getInstance().API()
                .getOrders(AccountManager.getInstance().getToken(), Constants.MAC_TEST, page, pageNum, typeVal)
                .compose(this.<BaseEntity<UnusualOrdersBean>>setThread())
                .subscribe(new Observer<BaseEntity<UnusualOrdersBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(BaseEntity<UnusualOrdersBean> unusualOrdersBeanBaseEntity) {
                        if(unusualOrdersBeanBaseEntity.isSuccess()){
                            orderList.addAll(unusualOrdersBeanBaseEntity.getData().list);
                            orderAdapter.notifyDataSetChanged();
                        }else {
                            showLoading(unusualOrdersBeanBaseEntity.getMsg());
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.abnormal_order_back_btn:
                finish();
                break;
        }
    }

    class OrderAdapter extends BaseAdapter {

        private Context context;
        private List<UnusualOrdersBean.Order> list;

        public OrderAdapter(Context context, List<UnusualOrdersBean.Order> list){
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

            UnusualOrdersBean.Order order = list.get(position);
            holder.orderNumberTv.setText(order.order_no);
            holder.dealTimeTv.setText(order.create_time);
            holder.sellerTv.setText(order.client_name);
            holder.buyerTv.setText(order.buyer_name);
            holder.weightTv.setText(order.total_amount);
            holder.incomeTv.setText(order.total_weight);
            holder.billingTv.setText(order.payment_type);
            holder.orderStatusTv.setText(order.status);

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
