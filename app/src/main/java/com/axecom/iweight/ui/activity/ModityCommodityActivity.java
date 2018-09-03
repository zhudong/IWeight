package com.axecom.iweight.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.bean.CommodityBean;
import com.axecom.iweight.bean.ScalesCategoryGoods;
import com.axecom.iweight.ui.view.SoftKey;

public class ModityCommodityActivity extends BaseActivity {

    private View rootView;

    private TextView idTv;
    private TextView nameTv;
    private EditText priceEt;
    private EditText traceableEt;
    private CheckedTextView isDefaultCtv;
    private Button confirmBtn;
    private Button cancelBtn;
    private SoftKey softKey;
    private CommodityBean bean;
    private int position;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.modity_commodity_price_layout, null);
        idTv = rootView.findViewById(R.id.modity_commodity_id_tv);
        nameTv = rootView.findViewById(R.id.modity_commodity_name_tv);
        priceEt = rootView.findViewById(R.id.modity_commodity_price_tv);
        traceableEt = rootView.findViewById(R.id.modity_commodity_traceable_code_tv);
        isDefaultCtv = rootView.findViewById(R.id.modity_commodity_is_default_tv);
        confirmBtn = rootView.findViewById(R.id.modity_commodity_confirm_btn);
        cancelBtn = rootView.findViewById(R.id.modity_commodity_cancel_btn);
        softKey = rootView.findViewById(R.id.modity_commodity_softkey);

        priceEt.requestFocus();
        disableShowInput(priceEt);
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        bean = (CommodityBean) intent.getExtras().getSerializable("commodityBean");
        position = intent.getIntExtra("position", -1);
        nameTv.setText(bean.getHotKeyGoods().name);
        idTv.setText(bean.getHotKeyGoods().id + "");
        priceEt.setText(bean.getHotKeyGoods().price + "");
        traceableEt.setText(bean.getHotKeyGoods().traceable_code + "");
        isDefaultCtv.setChecked(bean.getHotKeyGoods().is_default == 0 ? false : true);
        softKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getAdapter().getItem(position).toString();
                switch (rootView.findFocus().getId()){
                    case R.id.modity_commodity_price_tv:
                        setEditText(priceEt, position, text);
                        break;
                    case R.id.modity_commodity_traceable_code_tv:
                        setEditText(traceableEt, position, text);
                        break;
                }
            }
        });

        isDefaultCtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDefaultCtv.setChecked(!isDefaultCtv.isChecked());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modity_commodity_confirm_btn:
                ScalesCategoryGoods.HotKeyGoods goods = new ScalesCategoryGoods.HotKeyGoods();
                goods.id = bean.getHotKeyGoods().id;
                goods.cid = bean.getHotKeyGoods().cid;
                goods.name = bean.getHotKeyGoods().name;
                goods.price = priceEt.getText().toString();
                goods.traceable_code = Integer.parseInt(traceableEt.getText().toString());
                goods.is_default = isDefaultCtv.isChecked() ? 1 : 0;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("hotKeyGoods", goods);
                intent.putExtra("position", position);
                intent.putExtras(bundle);
                setResult(1001, intent);
                finish();
                break;
            case R.id.modity_commodity_cancel_btn:
                finish();
                break;

        }
    }
}
