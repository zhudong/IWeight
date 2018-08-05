package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.OrderListResultBean;
import com.axecom.iweight.bean.ReportResultBean;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.manager.MacManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018-5-24.
 */

public class DataSummaryActivity extends BaseActivity {

    private View rootView;
    private ListView dataListView;
    private ListView salesDetailsListView;
    private DataAdapter dataAdapter;
    private SalesAdapter salesAdapter;
    private LinearLayout reportTitleLayout;
    private LinearLayout reportTotalLayout;
    private LinearLayout salesTitleLayout;
    private LinearLayout salesTotalLayout;
    private TextView dayReportTv;
    private TextView monthReportTv;
    private TextView salesDetailsReportTv;
    private TextView backTv;
    private TextView dateTv;
    private List<ReportResultBean.list> dataList;
    private List<OrderListResultBean.list> orderList;

    private TextView countTotalTv, weightTotalTv, grandTotalTv, amountTotalTv;
    private TextView orderAmountTv;
    private Button prevPageBtn, nextPageBtn, prevMonthBtn, nextMonthBtn, prevDayBtn, nextDayBtn;
    private int currentPage = 1;
    private String currentDay;
    private int typeVal = 1;
    private int pageNum = 9;
    private int previousPos = 8;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.data_summary_activity, null);
        dataListView = rootView.findViewById(R.id.data_summary_listview);
        salesDetailsListView = rootView.findViewById(R.id.sales_details_listview);
        reportTitleLayout = rootView.findViewById(R.id.data_summary_reports_title_layout);
        reportTotalLayout = rootView.findViewById(R.id.data_summary_reports_total_layout);
        salesTitleLayout = rootView.findViewById(R.id.data_summary_sales_title_layout);
        salesTotalLayout = rootView.findViewById(R.id.data_summary_sales_total_layout);
        dayReportTv = rootView.findViewById(R.id.data_summary_day_report_tv);
        monthReportTv = rootView.findViewById(R.id.data_summary_month_report_tv);
        salesDetailsReportTv = rootView.findViewById(R.id.data_summary_sales_details_report_tv);
        backTv = rootView.findViewById(R.id.data_summary_back_tv);
        dateTv = rootView.findViewById(R.id.data_summary_date_tv);
        countTotalTv = rootView.findViewById(R.id.data_summary_reports_count_total_tv);
        orderAmountTv = rootView.findViewById(R.id.data_summary_order_amount_total_tv);
        weightTotalTv = rootView.findViewById(R.id.data_summary_reports_weight_total_tv);
        grandTotalTv = rootView.findViewById(R.id.data_summary_reports_grand_total_tv);
        amountTotalTv = rootView.findViewById(R.id.data_summary_reports_amount_total_tv);
        prevPageBtn = rootView.findViewById(R.id.data_summary_reports_prev_page_btn);
        nextPageBtn = rootView.findViewById(R.id.data_summary_reports_next_page_btn);
        prevMonthBtn = rootView.findViewById(R.id.data_summary_reports_prev_month_btn);
        nextMonthBtn = rootView.findViewById(R.id.data_summary_reports_next_month_btn);
        prevDayBtn = rootView.findViewById(R.id.data_summary_reports_prev_day_btn);
        nextDayBtn = rootView.findViewById(R.id.data_summary_reports_next_day_btn);


        dayReportTv.setOnClickListener(this);
        monthReportTv.setOnClickListener(this);
        salesDetailsReportTv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        prevPageBtn.setOnClickListener(this);
        nextPageBtn.setOnClickListener(this);
        prevMonthBtn.setOnClickListener(this);
        nextMonthBtn.setOnClickListener(this);
        prevDayBtn.setOnClickListener(this);
        nextDayBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        currentDay = getCurrentTime( "yyyy-MM-dd");
        getReportsList(currentDay, typeVal + "", currentPage + "", pageNum + "");
        dataList = new ArrayList<>();
        dataAdapter = new DataAdapter(this, dataList);
        dataListView.setAdapter(dataAdapter);

        orderList = new ArrayList<>();
        salesAdapter = new SalesAdapter(this, orderList);
        salesDetailsListView.setAdapter(salesAdapter);
        dateTv.setText(getCurrentTime("yyyy-MM-dd"));

    }

    public void getReportsList(final String dateVal, String typeVal, String page, final String pNum) {
        RetrofitFactory.getInstance().API()
                .getReportsList(AccountManager.getInstance().getAdminToken(), MacManager.getInstace(this).getMac(), dateVal, typeVal, page, pNum)
                .compose(this.<BaseEntity<ReportResultBean>>setThread())
                .subscribe(new Observer<BaseEntity<ReportResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(BaseEntity<ReportResultBean> reportResultBeanBaseEntity) {
                        if (reportResultBeanBaseEntity.isSuccess()) {
                            dataList.clear();
                            dataList.addAll(reportResultBeanBaseEntity.getData().list);
                            LogUtils.d("-----list size " + dataList.size());
                            dataAdapter.notifyDataSetChanged();
                            countTotalTv.setText(reportResultBeanBaseEntity.getData().total_num + "");
                            weightTotalTv.setText(reportResultBeanBaseEntity.getData().total_weight);
                            grandTotalTv.setText(reportResultBeanBaseEntity.getData().total_amount);
                            amountTotalTv.setText(reportResultBeanBaseEntity.getData().total_amount);
                            closeLoading();
                        } else {
                            showLoading(reportResultBeanBaseEntity.getMsg());
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
                        dataAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void getOrderList(String dateVal, String page, String pageNum) {
        RetrofitFactory.getInstance().API()
                .getOrderList(AccountManager.getInstance().getAdminToken(), MacManager.getInstace(this).getMac(), dateVal, page, pageNum)
                .compose(this.<BaseEntity<OrderListResultBean>>setThread())
                .subscribe(new Observer<BaseEntity<OrderListResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();

                    }

                    @Override
                    public void onNext(BaseEntity<OrderListResultBean> orderListResultBeanBaseEntity) {
                        if (orderListResultBeanBaseEntity.isSuccess()) {
                            orderAmountTv.setText(orderListResultBeanBaseEntity.getData().total_amount);
                            orderList.addAll(orderListResultBeanBaseEntity.getData().list);
                            salesAdapter.notifyDataSetChanged();
                        } else {
                            showLoading(orderListResultBeanBaseEntity.getMsg());
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
        switch (v.getId()) {
            case R.id.data_summary_day_report_tv:
                dataList.clear();
                currentPage = 1;
                typeVal = 1;
                reportTitleLayout.setVisibility(View.VISIBLE);
                reportTotalLayout.setVisibility(View.VISIBLE);
                dataListView.setVisibility(View.VISIBLE);
                salesTitleLayout.setVisibility(View.GONE);
                salesTotalLayout.setVisibility(View.GONE);
                salesDetailsListView.setVisibility(View.GONE);
                prevMonthBtn.setVisibility(View.GONE);
                nextMonthBtn.setVisibility(View.GONE);
                prevDayBtn.setVisibility(View.VISIBLE);
                nextDayBtn.setVisibility(View.VISIBLE);
                dayReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_gray_btn_bg));
                monthReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                salesDetailsReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                getReportsList(currentDay, typeVal + "", currentPage + "", pageNum + "");
                break;
            case R.id.data_summary_month_report_tv:
                dataList.clear();
                currentPage = 1;
                typeVal = 2;
                reportTitleLayout.setVisibility(View.VISIBLE);
                reportTotalLayout.setVisibility(View.VISIBLE);
                dataListView.setVisibility(View.VISIBLE);
                salesTitleLayout.setVisibility(View.GONE);
                salesTotalLayout.setVisibility(View.GONE);
                salesDetailsListView.setVisibility(View.GONE);
                prevMonthBtn.setVisibility(View.VISIBLE);
                nextMonthBtn.setVisibility(View.VISIBLE);
                prevDayBtn.setVisibility(View.GONE);
                nextDayBtn.setVisibility(View.GONE);
                dayReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                monthReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_gray_btn_bg));
                salesDetailsReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                getReportsList(currentDay, typeVal + "", currentPage + "", pageNum + "");
                break;
            case R.id.data_summary_sales_details_report_tv:
                orderList.clear();
                reportTitleLayout.setVisibility(View.GONE);
                reportTotalLayout.setVisibility(View.GONE);
                dataListView.setVisibility(View.GONE);
                salesTitleLayout.setVisibility(View.VISIBLE);
                salesTotalLayout.setVisibility(View.VISIBLE);
                salesDetailsListView.setVisibility(View.VISIBLE);
                dayReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                monthReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                salesDetailsReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_gray_btn_bg));
                getOrderList(getCurrentTime("yyyy-MM-dd"), "1", "10");
                break;
            case R.id.data_summary_back_tv:
                finish();
                break;
            case R.id.data_summary_reports_prev_page_btn:
                getReportsList(currentDay, typeVal + "", --currentPage <= 1 ? "1" : --currentPage + "", pageNum + "");
                break;
            case R.id.data_summary_reports_next_page_btn:
                getReportsList(currentDay, typeVal + "", ++currentPage + "", pageNum + "");
                break;
            case R.id.data_summary_reports_prev_month_btn:
                currentPage = 1;
                getReportsList(getCurrentTime(currentDay, "yyyy-MM", 1), typeVal + "", (currentPage = 1) + "", pageNum + "");
                break;
            case R.id.data_summary_reports_next_month_btn:
                currentPage = 1;
                getReportsList(getCurrentTime(currentDay, "yyyy-MM", 2), typeVal + "", (currentPage = 1) + "", pageNum + "");
                break;
            case R.id.data_summary_reports_prev_day_btn:
                currentDay = getCurrentTime(currentDay, "yyyy-MM-dd", 3);
                getReportsList(currentDay, typeVal + "", (currentPage = 1) + "", pageNum + "");
                break;
            case R.id.data_summary_reports_next_day_btn:
                currentDay = getCurrentTime(currentDay, "yyyy-MM-dd", 4);
                getReportsList(currentDay, typeVal + "", (currentPage = 1) + "", pageNum + "");
                break;
        }
    }


    class DataAdapter extends BaseAdapter {
        private Context context;
        private List<ReportResultBean.list> list;

        public DataAdapter(Context context, List<ReportResultBean.list> list) {
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
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.data_summary_item, null);
                holder = new ViewHolder();
                holder.timeTv = convertView.findViewById(R.id.data_item_time_tv);
                holder.countTv = convertView.findViewById(R.id.data_item_count_tv);
                holder.weightTv = convertView.findViewById(R.id.data_item_weight_tv);
                holder.grandTotalTv = convertView.findViewById(R.id.data_item_grand_total_tv);
                holder.incomeTv = convertView.findViewById(R.id.data_item_income_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ReportResultBean.list item = list.get(position);
            holder.timeTv.setText(item.times);
            holder.countTv.setText(item.all_num + "");
            holder.incomeTv.setText(item.total_amount);
            holder.grandTotalTv.setText(item.total_amount);
            holder.weightTv.setText(item.total_weight);
            return convertView;
        }

        class ViewHolder {
            TextView timeTv;
            TextView countTv;
            TextView weightTv;
            TextView grandTotalTv;
            TextView incomeTv;
        }
    }

    class SalesAdapter extends BaseAdapter {

        private Context context;
        private List<OrderListResultBean.list> list;


        public SalesAdapter(Context context, List<OrderListResultBean.list> list) {
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
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.sales_data_item, null);
                holder = new ViewHolder();
                holder.orderNumberTv = convertView.findViewById(R.id.sales_data_item_order_number_tv);
                holder.timeTv = convertView.findViewById(R.id.sales_data_item_time_tv);
                holder.weightTv = convertView.findViewById(R.id.sales_data_item_weight_tv);
                holder.grandTotalTv = convertView.findViewById(R.id.sales_data_item_grand_total_tv);
                holder.incomeTv = convertView.findViewById(R.id.sales_data_item_income_tv);
                holder.payTypeTv = convertView.findViewById(R.id.sales_data_item_pay_type_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OrderListResultBean.list item = list.get(position);
            holder.orderNumberTv.setText(item.order_no);
            holder.timeTv.setText(item.times);
            holder.weightTv.setText(item.total_weight);
            holder.grandTotalTv.setText(item.total_amount);
            holder.incomeTv.setText(item.total_amount);
            holder.payTypeTv.setText(item.payment_type);

            return convertView;
        }

        class ViewHolder {
            TextView orderNumberTv;
            TextView timeTv;
            TextView weightTv;
            TextView grandTotalTv;
            TextView incomeTv;
            TextView payTypeTv;
        }
    }
}
