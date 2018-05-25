package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

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


        dayReportTv.setOnClickListener(this);
        monthReportTv.setOnClickListener(this);
        salesDetailsReportTv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add(i + "点");
        }
        dataAdapter = new DataAdapter(this, list);
        dataListView.setAdapter(dataAdapter);

         List<String> list2 = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list2.add(i + " XXAsd123156461331015");
        }
        salesAdapter = new SalesAdapter(this, list2);
        salesDetailsListView.setAdapter(salesAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.data_summary_day_report_tv:
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

        }
    }

    class DataAdapter extends BaseAdapter{
        private Context context;
        private List<String> list;

        public DataAdapter(Context context, List<String> list){
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
                convertView = LayoutInflater.from(context).inflate(R.layout.data_summary_item, null);
                holder = new ViewHolder();
                holder.timeTv = convertView.findViewById(R.id.data_item_time_tv);
                holder.countTv = convertView.findViewById(R.id.data_item_count_tv);
                holder.weightTv = convertView.findViewById(R.id.data_item_weight_tv);
                holder.grandTotalTv = convertView.findViewById(R.id.data_item_grand_total_tv);
                holder.incomeTv = convertView.findViewById(R.id.data_item_income_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();

                holder.timeTv.setText(list.get(position));
            }
            return convertView;
        }

        class ViewHolder{
            TextView timeTv;
            TextView countTv;
            TextView weightTv;
            TextView grandTotalTv;
            TextView incomeTv;
        }
    }

    class SalesAdapter extends BaseAdapter{

        private Context context;
        private List<String> list;


        public SalesAdapter(Context context, List<String> list){
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
                convertView = LayoutInflater.from(context).inflate(R.layout.sales_data_item, null);
                holder = new ViewHolder();
                holder.orderNumberTv = convertView.findViewById(R.id.sales_data_item_order_number_tv);
                holder.timeTv = convertView.findViewById(R.id.sales_data_item_time_tv);
                holder.weightTv = convertView.findViewById(R.id.sales_data_item_weight_tv);
                holder.grandTotalTv = convertView.findViewById(R.id.sales_data_item_grand_total_tv);
                holder.incomeTv = convertView.findViewById(R.id.sales_data_item_income_tv);
                holder.payTypeTv = convertView.findViewById(R.id.sales_data_item_pay_type_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.orderNumberTv.setText(list.get(position));
            return convertView;
        }

        class ViewHolder{
            TextView orderNumberTv;
            TextView timeTv;
            TextView weightTv;
            TextView grandTotalTv;
            TextView incomeTv;
            TextView payTypeTv;
        }
    }
}
