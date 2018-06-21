package com.axecom.iweight.ui.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.bean.CommodityBean;

public class ModityCommodityActivity extends BaseActivity {

    private View rootView;

    private TextView nameTv;
    private Button confirmBtn;
    private Button cancelBtn;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.modity_commodity_price_layout, null);
        nameTv = rootView.findViewById(R.id.modity_commodity_name_tv);
        confirmBtn = rootView.findViewById(R.id.modity_commodity_confirm_btn);
        cancelBtn = rootView.findViewById(R.id.modity_commodity_cancel_btn);


        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        CommodityBean bean = (CommodityBean) intent.getExtras().getSerializable("commodityBean");
        nameTv.setText(bean.getHotKeyGoods().name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modity_commodity_confirm_btn:
                break;
            case R.id.modity_commodity_cancel_btn:
                finish();
                break;

        }
    }
}
