package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private DataAdapter dataAdapter;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.data_summary_activity, null);
        dataListView = rootView.findViewById(R.id.data_summary_listview);

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

    }

    @Override
    public void onClick(View v) {

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
}
