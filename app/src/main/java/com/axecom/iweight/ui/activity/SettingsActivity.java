package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.bean.SettingsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-5-16.
 */

public class SettingsActivity extends BaseActivity {

    private static final int[] ICONS = {R.drawable.switching_setting,
                                        R.drawable.patch_setting,
                                        R.drawable.reports_setting,
                                        R.drawable.server_setting,
                                        R.drawable.order_setting,
                                        R.drawable.abnormal_setting,
                                        R.drawable.bd_setting,
                                        R.drawable.commodity_setting,
                                        R.drawable.simple_mode_setting,
                                        R.drawable.update_setting,
                                        R.drawable.wifi_setting,
                                        R.drawable.local_setting,
                                        R.drawable.system_setting,
                                        R.drawable.payment_setting,
                                        R.drawable.weight_setting};

    private static final int[] TITLES = {R.string.string_switching_setting_txt,
                                        R.string.string_patch_setting_txt,
                                        R.string.string_reports_setting_txt,
                                        R.string.string_server_setting_txt,
                                        R.string.string_order_setting_txt,
                                        R.string.string_abnormal_setting_txt,
                                        R.string.string_bd_setting_txt,
                                        R.string.string_commodity_setting_txt,
                                        R.string.string_simple_mode_setting_txt,
                                        R.string.string_update_setting_txt,
                                        R.string.string_wifi_setting_txt,
                                        R.string.string_local_setting_txt,
                                        R.string.string_system_setting_txt,
                                        R.string.string_payment_setting_txt,
                                        R.string.string_weight_setting_txt};

    private View rootView;
    private GridView settingsGV;
    private SettingsAdapter settingsAdapter;


    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.settings_activity, null);
        settingsGV = rootView.findViewById(R.id.settings_grid_view);


        return rootView;
    }

    @Override
    public void initView() {
        List<SettingsBean> settngsList = new ArrayList<>();
        for (int i = 0; i < ICONS.length; i++) {
            SettingsBean bean = new SettingsBean();
            bean.setIcon(ICONS[i]);
            bean.setTitle(TITLES[i]);
            settngsList.add(bean);
        }
        settingsAdapter = new SettingsAdapter(this, settngsList);
        settingsGV.setAdapter(settingsAdapter);
        settingsGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 4){
                    startDDMActivity(OrderInvalidActivity.class, false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    class SettingsAdapter extends BaseAdapter{

        private Context context;
        private List<SettingsBean> settingList;

        public SettingsAdapter(Context context, List<SettingsBean> settingList){
            this.context = context;
            this.settingList = settingList;
        }

        @Override
        public int getCount() {
            return settingList.size();
        }

        @Override
        public Object getItem(int position) {
            return settingList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.settings_item, null);
                holder = new ViewHolder();
                holder.iconIv = convertView.findViewById(R.id.settings_item_icon_iv);
                holder.titleTv = convertView.findViewById(R.id.settings_item_title_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            SettingsBean item = settingList.get(position);
            holder.iconIv.setImageDrawable(ContextCompat.getDrawable(context, item.getIcon()));
            holder.titleTv.setText(getString(item.getTitle()));
            return convertView;
        }

        class ViewHolder{
            ImageView iconIv;
            TextView titleTv;
        }
    }
}
