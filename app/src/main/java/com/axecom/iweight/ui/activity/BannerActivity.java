package com.axecom.iweight.ui.activity;

import android.app.Presentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.bean.SubOrderReqBean;
import com.axecom.iweight.net.RetrofitFactory;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/20.
 */

public class BannerActivity extends Presentation {

    private static final int[] banners = {R.drawable.logo, R.drawable.login_bg, R.drawable.banner_bg};

    public ConvenientBanner convenientBanner;
    private Context context;
    private int currentPos = 0;
    public ListView orderListView;
    public TextView bannerTotalPriceTv;
    public ImageView bannerQRCode;
    public LinearLayout bannerOrderLayout;
    public List<SubOrderReqBean.Goods> goodsList;
    public MyAdapter adapter;
    public List<String> list;


    public BannerActivity(Context outerContext, Display display) {
        super(outerContext, display);
        this.context = outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banner_activity);
        orderListView = findViewById(R.id.banner_order_listview);
        bannerQRCode = findViewById(R.id.banner_qrcode_iv);
        bannerTotalPriceTv = findViewById(R.id.banner_total_price_tv);
        bannerOrderLayout = findViewById(R.id.banner_order_layout);
        convenientBanner = findViewById(R.id.banner_convenient_banner);
        List<Integer> localImages = new ArrayList<>();
        localImages.add(R.drawable.logo);
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages);

        goodsList = new ArrayList<>();
        adapter = new MyAdapter(context, goodsList);
        orderListView.setAdapter(adapter);

    }

    public class LocalImageHolderView implements Holder<Integer> {
        private ImageView imageView;
        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            imageView.setImageResource(data);
        }
    }

    class MyAdapter extends BaseAdapter {
        private Context context;
        private List<SubOrderReqBean.Goods> list;

        public MyAdapter(Context context, List<SubOrderReqBean.Goods> list) {
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.commodity_item, null);
                holder = new ViewHolder();
                holder.nameTv = convertView.findViewById(R.id.commodity_name_tv);
                holder.priceTv = convertView.findViewById(R.id.commodity_price_tv);
                holder.weightTv = convertView.findViewById(R.id.commodity_weight_tv);
                holder.subtotalTv = convertView.findViewById(R.id.commodity_subtotal_tv);
                holder.deleteBtn = convertView.findViewById(R.id.commodity_delete_btn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SubOrderReqBean.Goods goods = list.get(position);
            holder.nameTv.setText(goods.getGoods_name());
            holder.weightTv.setText(goods.getGoods_weight());
            holder.priceTv.setText(goods.getGoods_price());
            holder.subtotalTv.setText(goods.getGoods_amount());

            return convertView;
        }

        class ViewHolder {
            TextView nameTv;
            TextView priceTv;
            TextView weightTv;
            TextView subtotalTv;
            Button deleteBtn;

        }
    }
}
