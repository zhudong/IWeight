package com.axecom.iweight.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.bean.ChooseBean;
import com.axecom.iweight.ui.view.ChooseDialog;
import com.axecom.iweight.ui.view.SoftKeyborad;

import java.util.ArrayList;
import java.util.List;

public class SystemSettingsActivity extends BaseActivity {

    private View rootView;
    private Button loginTypeBtn, printerBtn, balanceRoundingBtn, priceingMethodBtn, weightRoundingBtn, weightUnitBtn;
    private TextView buyerNumberTv, sellerNumberTv;
    private TextView loginTypeTv, printerTv, balanceRoundingTv, priceingMethodTv, weightRoundingTv, weightUnitTv;
    private Button backBtn, saveBtn;
    private CheckedTextView notClearPriceCtv, saveWeightCtv, autoObtainCtv, cashEttlementCtv, distinguishCtv, icCardSettlementCtv, stopPrintCtv, noPatchSettlementCtv, autoPrevCtv, cashRoundingCtv, stopCashCtv;


    private List<ChooseBean> list;
    private int loginTypePos = 0,printerPos = 0,balanceRoundingPos = 0,priceingMethodPos = 0,weightRoundingPos = 0,weightUnitPos = 0;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.system_settings_activity, null);
        loginTypeBtn = rootView.findViewById(R.id.system_settings_login_type_btn);
        printerBtn = rootView.findViewById(R.id.system_settings_printer_btn);
        balanceRoundingBtn = rootView.findViewById(R.id.system_settings_balance_rounding_btn);
        priceingMethodBtn = rootView.findViewById(R.id.system_settings_default_priceing_method_btn);
        weightRoundingBtn = rootView.findViewById(R.id.system_settings_weight_rounding_btn);
        weightUnitBtn = rootView.findViewById(R.id.system_settings_weight_unit_btn);

        buyerNumberTv = rootView.findViewById(R.id.system_settings_default_buyer_number_tv);
        sellerNumberTv = rootView.findViewById(R.id.system_settings_default_seller_number_tv);

        loginTypeTv = rootView.findViewById(R.id.system_settings_login_type_tv);
        printerTv = rootView.findViewById(R.id.system_settings_printer_tv);
        balanceRoundingTv = rootView.findViewById(R.id.system_settings_balance_rounding_tv);
        priceingMethodTv = rootView.findViewById(R.id.system_settings_default_priceing_method_tv);
        weightRoundingTv = rootView.findViewById(R.id.system_settings_weight_rounding_tv);
        weightUnitTv = rootView.findViewById(R.id.system_settings_weight_unit_tv);

        notClearPriceCtv = rootView.findViewById(R.id.system_settings_not_clear_price_ctv);
        saveWeightCtv = rootView.findViewById(R.id.system_settings_save_weight_ctv);
        autoObtainCtv = rootView.findViewById(R.id.system_settings_auto_obtain_ctv);
        cashEttlementCtv = rootView.findViewById(R.id.system_settings_cash_ettlement_ctv);
        distinguishCtv = rootView.findViewById(R.id.system_settings_distinguish_ctv);
        icCardSettlementCtv = rootView.findViewById(R.id.system_settings_ic_card_settlement_ctv);
        stopPrintCtv = rootView.findViewById(R.id.system_settings_stop_print_ctv);
        noPatchSettlementCtv = rootView.findViewById(R.id.system_settings_no_patch_settlement_ctv);
        autoPrevCtv = rootView.findViewById(R.id.system_settings_auto_prev_ctv);
        cashRoundingCtv = rootView.findViewById(R.id.system_settings_cash_rounding_ctv);
        stopCashCtv = rootView.findViewById(R.id.system_settings_stop_cash_ctv);

        backBtn = rootView.findViewById(R.id.system_settings_back_btn);
        saveBtn = rootView.findViewById(R.id.system_settings_save_btn);

        loginTypeBtn.setOnClickListener(this);
        printerBtn.setOnClickListener(this);
        balanceRoundingBtn.setOnClickListener(this);
        priceingMethodBtn.setOnClickListener(this);
        weightRoundingBtn.setOnClickListener(this);
        weightUnitBtn.setOnClickListener(this);
        buyerNumberTv.setOnClickListener(this);
        sellerNumberTv.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        notClearPriceCtv.setOnClickListener(this);
        saveWeightCtv.setOnClickListener(this);
        autoObtainCtv.setOnClickListener(this);
        cashEttlementCtv.setOnClickListener(this);
        distinguishCtv.setOnClickListener(this);
        stopPrintCtv.setOnClickListener(this);
        noPatchSettlementCtv.setOnClickListener(this);
        autoPrevCtv.setOnClickListener(this);
        cashRoundingCtv.setOnClickListener(this);
        stopCashCtv.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void initView() {
        list = new ArrayList<>();
        ChooseBean bean;
        for (int i = 0; i < 4; i++) {
            bean= new ChooseBean();
            bean.setChecked(false);
            bean.setChooseItem("测试数据 " + i);
            list.add(bean);
        }
    }

    @Override
    public void onClick(View v) {
        ChooseDialog.Builder chooseBuilder = new ChooseDialog.Builder(this);
        SoftKeyborad.Builder softBuilder = new SoftKeyborad.Builder(this);
        switch (v.getId()) {
            case R.id.system_settings_login_type_btn:
                chooseBuilder.create(list, loginTypePos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                         loginTypePos = position;
                         loginTypeTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.system_settings_printer_btn:
                chooseBuilder.create(list, printerPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        printerPos = position;
                        printerTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.system_settings_balance_rounding_btn:
                chooseBuilder.create(list, balanceRoundingPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        balanceRoundingPos = position;
                        balanceRoundingTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.system_settings_default_priceing_method_btn:
                chooseBuilder.create(list, priceingMethodPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        priceingMethodPos = position;
                        priceingMethodTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.system_settings_weight_rounding_btn:
                chooseBuilder.create(list, weightRoundingPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        weightRoundingPos = position;
                        weightRoundingTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.system_settings_weight_unit_btn:
                chooseBuilder.create(list, weightUnitPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        weightUnitPos = position;
                        weightRoundingTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.system_settings_default_buyer_number_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        buyerNumberTv.setText(result);
                    }
                }).show();
                break;
            case R.id.system_settings_default_seller_number_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        sellerNumberTv.setText(result);
                    }
                }).show();
                break;
            case R.id.system_settings_not_clear_price_ctv:
                notClearPriceCtv.setChecked(!notClearPriceCtv.isChecked());
                break;
            case R.id.system_settings_save_weight_ctv:
                saveWeightCtv.setChecked(!saveWeightCtv.isChecked());
                break;
            case R.id.system_settings_auto_obtain_ctv:
                autoObtainCtv.setChecked(!autoObtainCtv.isChecked());
                break;
            case R.id.system_settings_cash_ettlement_ctv:
                cashEttlementCtv.setChecked(!cashEttlementCtv.isChecked());
                break;
            case R.id.system_settings_distinguish_ctv:
                distinguishCtv.setChecked(!distinguishCtv.isChecked());
                break;
            case R.id.system_settings_ic_card_settlement_ctv:
                icCardSettlementCtv.setChecked(!icCardSettlementCtv.isChecked());
                break;
            case R.id.system_settings_stop_print_ctv:
                stopPrintCtv.setChecked(!stopPrintCtv.isChecked());
                break;
            case R.id.system_settings_no_patch_settlement_ctv:
                noPatchSettlementCtv.setChecked(!noPatchSettlementCtv.isChecked());
                break;
            case R.id.system_settings_auto_prev_ctv:
                autoPrevCtv.setChecked(!autoPrevCtv.isChecked());
                break;
            case R.id.system_settings_cash_rounding_ctv:
                cashRoundingCtv.setChecked(!cashRoundingCtv.isChecked());
                break;
            case R.id.system_settings_stop_cash_ctv:
                stopCashCtv.setChecked(!stopCashCtv.isChecked());
                break;
            case R.id.system_settings_back_btn:
                finish();
                break;
            case R.id.system_settings_save_btn:
                boolean notClear = notClearPriceCtv.isChecked();
                break;
        }
    }
}
