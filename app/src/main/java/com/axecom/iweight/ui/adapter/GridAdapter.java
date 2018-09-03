package com.axecom.iweight.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.bean.ScalesCategoryGoods;

import java.util.List;

/**
 * Created by Administrator on 2018-5-16.
 */

public class GridAdapter extends BaseAdapter{
    private Context context;
    private List<ScalesCategoryGoods.HotKeyGoods> list;
    private int pos = -1;

    public GridAdapter(Context context, List<ScalesCategoryGoods.HotKeyGoods> list){
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
    public void setCheckedAtPosition(int position) {
        this.pos = position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.main_grid_item, null);
            holder = new ViewHolder();
            holder.commodityBtn = convertView.findViewById(R.id.main_grid_item_btn);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScalesCategoryGoods.HotKeyGoods goods = list.get(position);
        holder.commodityBtn.setText(goods.name);
        if (pos == position) {
            holder.commodityBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_green_bg));
            holder.commodityBtn.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.commodityBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_weight_display_bg));
            holder.commodityBtn.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        return convertView;
    }

    class ViewHolder{
        TextView commodityBtn;
    }
}