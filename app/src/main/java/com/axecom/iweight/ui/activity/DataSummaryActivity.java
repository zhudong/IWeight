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
import com.axecom.iweight.bean.ReportResultBean;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.net.RetrofitFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
    private List<ReportResultBean.list> dataList;

    private TextView countTotalTv, weightTotalTv, grandTotalTv, amountTotalTv;
    private Button prevPageBtn, nextPageBtn, prevMonthBtn, nextMonthBtn;
    private int currentPage = 1;
    private int typeVal = 1;
    private int pageNum = 10;

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
        countTotalTv = rootView.findViewById(R.id.data_summary_reports_count_total_tv);
        weightTotalTv = rootView.findViewById(R.id.data_summary_reports_weight_total_tv);
        grandTotalTv = rootView.findViewById(R.id.data_summary_reports_grand_total_tv);
        amountTotalTv = rootView.findViewById(R.id.data_summary_reports_amount_total_tv);
        prevPageBtn = rootView.findViewById(R.id.data_summary_reports_prev_page_btn);
        nextPageBtn = rootView.findViewById(R.id.data_summary_reports_next_page_btn);
        prevMonthBtn = rootView.findViewById(R.id.data_summary_reports_prev_month_btn);
        nextMonthBtn = rootView.findViewById(R.id.data_summary_reports_next_month_btn);


        dayReportTv.setOnClickListener(this);
        monthReportTv.setOnClickListener(this);
        salesDetailsReportTv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        countTotalTv.setOnClickListener(this);
        weightTotalTv.setOnClickListener(this);
        grandTotalTv.setOnClickListener(this);
        amountTotalTv.setOnClickListener(this);
        prevPageBtn.setOnClickListener(this);
        nextPageBtn.setOnClickListener(this);
        prevMonthBtn.setOnClickListener(this);
        nextMonthBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        getReportsList(0, getCurrentTime("yyyy-MM"), typeVal + "", currentPage + "", pageNum + "");
        dataList = new ArrayList<>();
        dataAdapter = new DataAdapter(this, dataList);
        dataListView.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list2.add(i + " XXAsd123156461331015");
        }
        salesAdapter = new SalesAdapter(this, list2);
        salesDetailsListView.setAdapter(salesAdapter);


    }

    public void getReportsList(final int type, String dateVal, String typeVal, String page, final String pNum) {
        showLoading();
        RetrofitFactory.getInstance().API()
                .getReportsList(Constants.MAC_TEST, dateVal, typeVal, page, pNum)
                .compose(this.<BaseEntity<ReportResultBean>>setThread())
                .subscribe(new Observer<BaseEntity<ReportResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseEntity<ReportResultBean> reportResultBeanBaseEntity) {
                        if (reportResultBeanBaseEntity.isSuccess()) {
                            dataList.addAll(reportResultBeanBaseEntity.getData().list);
                            dataAdapter.notifyDataSetChanged();
//                            if (type == 1)
//                                scrollTo(dataListView, dataListView.getFirstVisiblePosition() - pageNum <= 0 ? 0 : dataListView.getFirstVisiblePosition() - pageNum);
                            if (type == 2){
                                scrollTo(dataListView, dataListView.getFirstVisiblePosition() + pageNum);
                            }
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
                getReportsList(0, getCurrentTime("yyyy-MM"), typeVal + "", currentPage + "", pageNum + "");
                reportTitleLayout.setVisibility(View.VISIBLE);
                reportTotalLayout.setVisibility(View.VISIBLE);
                dataListView.setVisibility(View.VISIBLE);
                salesTitleLayout.setVisibility(View.GONE);
                salesTotalLayout.setVisibility(View.GONE);
                salesDetailsListView.setVisibility(View.GONE);
                dayReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_gray_btn_bg));
                monthReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                salesDetailsReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
//                dayReportTv.getPaint().setFakeBoldText(true);
//                monthReportTv.getPaint().setFakeBoldText(false);
//                salesDetailsReportTv.getPaint().setFakeBoldText(false);
                break;
            case R.id.data_summary_month_report_tv:
                dataList.clear();
                currentPage = 1;
                typeVal = 2;
                getReportsList(0, getCurrentTime("yyyy-MM"), typeVal + "", currentPage + "", pageNum + "");
                reportTitleLayout.setVisibility(View.VISIBLE);
                reportTotalLayout.setVisibility(View.VISIBLE);
                dataListView.setVisibility(View.VISIBLE);
                salesTitleLayout.setVisibility(View.GONE);
                salesTotalLayout.setVisibility(View.GONE);
                salesDetailsListView.setVisibility(View.GONE);
                dayReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                monthReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_gray_btn_bg));
                salesDetailsReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
//                dayReportTv.getPaint().setFakeBoldText(false);
//                monthReportTv.getPaint().setFakeBoldText(true);
//                salesDetailsReportTv.getPaint().setFakeBoldText(false);
                break;
            case R.id.data_summary_sales_details_report_tv:
                reportTitleLayout.setVisibility(View.GONE);
                reportTotalLayout.setVisibility(View.GONE);
                dataListView.setVisibility(View.GONE);
                salesTitleLayout.setVisibility(View.VISIBLE);
                salesTotalLayout.setVisibility(View.VISIBLE);
                salesDetailsListView.setVisibility(View.VISIBLE);
                dayReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                monthReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                salesDetailsReportTv.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_gray_btn_bg));
//                dayReportTv.getPaint().setFakeBoldText(false);
//                monthReportTv.getPaint().setFakeBoldText(false);
//                salesDetailsReportTv.getPaint().setFakeBoldText(true);
                break;
            case R.id.data_summary_back_tv:
                finish();
                break;
            case R.id.data_summary_reports_prev_page_btn:
//                if (currentPage == 1)
//                    return;
                scrollTo(dataListView, dataListView.getFirstVisiblePosition() - pageNum <= 0 ? 0 : dataListView.getFirstVisiblePosition() - pageNum);

//                getReportsList(1, getCurrentTime("yyyy-MM"), typeVal + "", (--currentPage == 1 ? 1 : --currentPage) + "", pageNum + "");
                break;
            case R.id.data_summary_reports_next_page_btn:
                getReportsList(2, getCurrentTime("yyyy-MM"), typeVal + "", ++currentPage + "", pageNum + "");
                break;
            case R.id.data_summary_reports_prev_month_btn:
                currentPage = 1;
                getReportsList(0, getCurrentTime("yyyy-MM", 1), typeVal + "", currentPage + "", pageNum + "");
                break;
            case R.id.data_summary_reports_next_month_btn:
                currentPage = 1;
                getReportsList(0, getCurrentTime("yyyy-MM", 2), typeVal + "", currentPage + "", pageNum + "");
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
                Map item = (Map) list.get(position);
                holder.timeTv.setText(item.get("times").toString());
                holder.countTv.setText(item.get("all_num").toString());
                holder.incomeTv.setText(item.get("total_amount").toString());
                holder.grandTotalTv.setText(item.get("total_amount").toString());
                holder.weightTv.setText(item.get("total_weight").toString());
            }
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
        private List<String> list;


        public SalesAdapter(Context context, List<String> list) {
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

            holder.orderNumberTv.setText(list.get(position));
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
