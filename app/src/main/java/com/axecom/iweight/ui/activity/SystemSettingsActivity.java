package com.axecom.iweight.ui.activity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.ChooseBean;
import com.axecom.iweight.bean.SettingDataBean;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.manager.MacManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.view.ChooseDialog;
import com.axecom.iweight.ui.view.SoftKeyborad;
import com.axecom.iweight.utils.SPUtils;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SystemSettingsActivity extends BaseActivity {

    private static final String KEY_DEFAULT_LOGIN_TYPE = "key_default_login_type";
    private static final String KEY_PRINTER = "key_printer";
    private static final String KEY_BUYER_NUMBER = "key_buyer_number";
    private static final String KEY_BALANCE_ROUNDING = "key_balance_rounding";
    private static final String KEY_PRICEING_METHOD = "key_priceing_method";
    private static final String KEY_WEIGHT_ROUNDING = "key_weight_rounding";
    private static final String KEY_SELLER_NUMBER = "key_seller_number";
    private static final String KEY_WEIGHT_UNIT = "key_weight_unit";

    private static final String KEY_NOT_CLEAR = "key_not_clear";
    private static final String KEY_SAVE_WEIGHT = "key_save_weight";
    private static final String KEY_AUTO_OBTAIN = "key_auto_obtain";
    private static final String KEY_CASH_SETTLEMENT = "key_cash_settlement";
    private static final String KEY_DISTINGUISH = "key_distinguish";
    private static final String KEY_ICCARD_SETTLEMENT = "key_iccard_settlement";
    private static final String KEY_STOP_PRINT = "key_stop_print";
    private static final String KEY_NO_PATCH_SETTLEMENT = "key_no_patch_settlement";
    private static final String KEY_AUTO_PREV = "key_auto_prev";
    private static final String KEY_CASH_ROUNDING = "key_cash_rounding";
    private static final String KEY_STOP_CASH = "key_stop_cash";


    private View rootView;
    private Button loginTypeBtn, printerBtn, balanceRoundingBtn, priceingMethodBtn, weightRoundingBtn, weightUnitBtn;
    private TextView buyerNumberTv, sellerNumberTv;
    private TextView loginTypeTv, printerTv, balanceRoundingTv, priceingMethodTv, weightRoundingTv, weightUnitTv;
    private Button backBtn, saveBtn;
    private CheckedTextView notClearPriceCtv, saveWeightCtv, autoObtainCtv, cashEttlementCtv, distinguishCtv, icCardSettlementCtv, stopPrintCtv, noPatchSettlementCtv, autoPrevCtv, cashRoundingCtv, stopCashCtv;


    List<Map<String, String>> loginTypeList = new ArrayList<>();
    List<Map<String, String>> pricingModelList = new ArrayList<>();
    List<Map<String, String>> printerList = new ArrayList<>();
    List<Map<String, String>> roundingWeightList = new ArrayList<>();
    List<Map<String, String>> screenUnitList = new ArrayList<>();
    List<Map<String, String>> balanceRoundingList = new ArrayList<>();
    private int loginTypePos = 0, printerPos = 0, balanceRoundingPos = 0, priceingMethodPos = 0, weightRoundingPos = 0, weightUnitPos = 0;
    LinkedTreeMap valueMap;

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
        loginTypeTv.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void initView() {
        getSettingData();
    }

    @Override
    public void onClick(View v) {
        ChooseDialog.Builder chooseBuilder = new ChooseDialog.Builder(this);
        SoftKeyborad.Builder softBuilder = new SoftKeyborad.Builder(this);
        switch (v.getId()) {
            case R.id.system_settings_login_type_btn:
                chooseBuilder.create(loginTypeList, loginTypePos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        loginTypePos = position;
                        loginTypeTv.setText(((Map<String, String>) parent.getAdapter().getItem(position)).get(position + 1 + ""));
                    }
                }).show();
                break;
            case R.id.system_settings_printer_btn:
                chooseBuilder.create(printerList, printerPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        printerPos = position;
                        printerTv.setText(((Map<String, String>) parent.getAdapter().getItem(position)).get(position + 1 + ""));
                    }
                }).show();
                break;
            case R.id.system_settings_balance_rounding_btn:
                chooseBuilder.create(balanceRoundingList, balanceRoundingPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        balanceRoundingPos = position;
                        balanceRoundingTv.setText(((Map<String, String>) parent.getAdapter().getItem(position)).get(position + 1 + ""));
                    }
                }).show();
                break;
            case R.id.system_settings_default_priceing_method_btn:
                chooseBuilder.create(pricingModelList, priceingMethodPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        priceingMethodPos = position;
                        priceingMethodTv.setText(((Map<String, String>) parent.getAdapter().getItem(position)).get(position + 1 + ""));
                    }
                }).show();
                break;
            case R.id.system_settings_weight_rounding_btn:
                chooseBuilder.create(roundingWeightList, weightRoundingPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        weightRoundingPos = position;
                        weightRoundingTv.setText(((Map<String, String>) parent.getAdapter().getItem(position)).get(position + 1 + ""));
                    }
                }).show();
                break;
            case R.id.system_settings_weight_unit_btn:
                chooseBuilder.create(screenUnitList, weightUnitPos, new ChooseDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        weightUnitPos = position;
                        weightUnitTv.setText(((Map<String, String>) parent.getAdapter().getItem(position)).get(position + 1 + ""));
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
            case R.id.system_settings_login_type_tv:
            case R.id.system_settings_save_btn:
                saveSettingsToSP();
                break;
        }
    }

    public void saveSettingsToSP() {
        if (valueMap == null)
            return;

        LinkedHashMap v = new LinkedHashMap();
        v.put("update_time", System.currentTimeMillis());
        v.put("val", loginTypeTv.getText().toString());
        valueMap.put("default_login_type", v);
        SPUtils.saveObject(this, KEY_DEFAULT_LOGIN_TYPE, valueMap.get("default_login_type"));


        LinkedHashMap printerMap = new LinkedHashMap();
        printerMap.put("update_time", System.currentTimeMillis());
        printerMap.put("val", printerTv.getText().toString());
        valueMap.put("printer_configuration", printerMap);
        SPUtils.saveObject(this, KEY_PRINTER, valueMap.get("printer_configuration"));

        LinkedHashMap buyerNumberMap = new LinkedHashMap();
        buyerNumberMap.put("update_time", System.currentTimeMillis());
        buyerNumberMap.put("val", printerTv.getText().toString());
        valueMap.put("default_buyer_number", buyerNumberMap);
        SPUtils.saveObject(this, KEY_BUYER_NUMBER, valueMap.get("default_buyer_number"));

        LinkedHashMap balanceRoundingMap = new LinkedHashMap();
        balanceRoundingMap.put("update_time", System.currentTimeMillis());
        balanceRoundingMap.put("val", printerTv.getText().toString());
        valueMap.put("balance_rounding", balanceRoundingMap);
        SPUtils.saveObject(this, KEY_BUYER_NUMBER, valueMap.get("balance_rounding"));

        LinkedHashMap priceingMethodMap = new LinkedHashMap();
        priceingMethodMap.put("update_time", System.currentTimeMillis());
        priceingMethodMap.put("val", printerTv.getText().toString());
        valueMap.put("default_pricing_model", priceingMethodMap);
        SPUtils.saveObject(this, KEY_PRICEING_METHOD, valueMap.get("default_pricing_model"));


        LinkedHashMap weightRoundingMap = new LinkedHashMap();
        weightRoundingMap.put("update_time", System.currentTimeMillis());
        weightRoundingMap.put("val", printerTv.getText().toString());
        valueMap.put("rounding_weight", weightRoundingMap);
        SPUtils.saveObject(this, KEY_WEIGHT_ROUNDING, valueMap.get("rounding_weight"));

        LinkedHashMap sellerNumberMap = new LinkedHashMap();
        sellerNumberMap.put("update_time", System.currentTimeMillis());
        sellerNumberMap.put("val", printerTv.getText().toString());
        valueMap.put("default_seller_number", sellerNumberMap);
        SPUtils.saveObject(this, KEY_SELLER_NUMBER, valueMap.get("default_seller_number"));


       /* if (!TextUtils.equals(buyerNumberTv.getText().toString(), ((LinkedTreeMap) SPUtils.get(this, KEY_BUYER_NUMBER, null)).get("default_buyer_number").toString())) {
            ((LinkedTreeMap) valueMap.get("default_buyer_number")).put("update_time", System.currentTimeMillis());
            ((LinkedTreeMap) valueMap.get("default_buyer_number")).put("val", buyerNumberTv.getText().toString());
            SPUtils.put(this, KEY_BUYER_NUMBER, valueMap.get("default_buyer_number"));

        }
        if (!TextUtils.equals(balanceRoundingTv.getText().toString(), ((LinkedTreeMap) SPUtils.get(this, KEY_BALANCE_ROUNDING, null)).get("balance_rounding").toString())) {
            ((LinkedTreeMap) valueMap.get("balance_rounding")).put("update_time", System.currentTimeMillis());
            ((LinkedTreeMap) valueMap.get("balance_rounding")).put("val", balanceRoundingTv.getText().toString());
            SPUtils.put(this, KEY_BUYER_NUMBER, valueMap.get("balance_rounding"));

        }
        if (!TextUtils.equals(priceingMethodTv.getText().toString(), ((LinkedTreeMap) SPUtils.get(this, KEY_PRICEING_METHOD, null)).get("default_pricing_model").toString())) {
            ((LinkedTreeMap) valueMap.get("default_pricing_model")).put("update_time", System.currentTimeMillis());
            ((LinkedTreeMap) valueMap.get("default_pricing_model")).put("val", priceingMethodTv.getText().toString());
            SPUtils.put(this, KEY_PRICEING_METHOD, valueMap.get("default_pricing_model"));

        }
        if (!TextUtils.equals(weightRoundingTv.getText().toString(), ((LinkedTreeMap) SPUtils.get(this, KEY_WEIGHT_ROUNDING, null)).get("rounding_weight").toString())) {
            ((LinkedTreeMap) valueMap.get("rounding_weight")).put("update_time", System.currentTimeMillis());
            ((LinkedTreeMap) valueMap.get("rounding_weight")).put("val", weightRoundingTv.getText().toString());
            SPUtils.put(this, KEY_WEIGHT_ROUNDING, valueMap.get("rounding_weight"));

        }
        if (!TextUtils.equals(sellerNumberTv.getText().toString(), ((LinkedTreeMap) SPUtils.get(this, KEY_SELLER_NUMBER, null)).get("default_seller_number").toString())) {
            ((LinkedTreeMap) valueMap.get("default_seller_number")).put("update_time", System.currentTimeMillis());
            ((LinkedTreeMap) valueMap.get("default_seller_number")).put("val", sellerNumberTv.getText().toString());
            SPUtils.put(this, KEY_SELLER_NUMBER, valueMap.get("default_seller_number"));

        }
        if (!TextUtils.equals(weightUnitTv.getText(), ((LinkedTreeMap) SPUtils.get(this, KEY_WEIGHT_UNIT, null)).get("screen_unit_display").toString())) {
            ((LinkedTreeMap) valueMap.get("screen_unit_display")).put("update_time", System.currentTimeMillis());
            ((LinkedTreeMap) valueMap.get("screen_unit_display")).put("val", weightUnitTv.getText().toString());
            SPUtils.put(this, KEY_WEIGHT_UNIT, valueMap.get("screen_unit_display"));
        }


        if ((notClearPriceCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_NOT_CLEAR, null)).get("price_after_saving"))) {
            ((LinkedTreeMap) valueMap.get("price_after_saving")).put("update_time", System.currentTimeMillis());
            ((LinkedTreeMap) valueMap.get("price_after_saving")).put("val", notClearPriceCtv.isChecked());
            SPUtils.put(this, KEY_NOT_CLEAR, valueMap.get("price_after_saving"));
            if ((saveWeightCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_SAVE_WEIGHT, null)).get("confirm_the_preservation"))) {
                ((LinkedTreeMap) valueMap.get("confirm_the_preservation")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("confirm_the_preservation")).put("val", saveWeightCtv.isChecked());
                SPUtils.put(this, KEY_SAVE_WEIGHT, valueMap.get("confirm_the_preservation"));
            }
            if ((autoObtainCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_AUTO_OBTAIN, null)).get("buyers_and_sellers_by_default"))) {
                ((LinkedTreeMap) valueMap.get("buyers_and_sellers_by_default")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("buyers_and_sellers_by_default")).put("val", autoObtainCtv.isChecked());
                SPUtils.put(this, KEY_AUTO_OBTAIN, valueMap.get("buyers_and_sellers_by_default"));
            }
            if ((cashEttlementCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_CASH_SETTLEMENT, null)).get("online_settlement"))) {
                ((LinkedTreeMap) valueMap.get("online_settlement")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("online_settlement")).put("val", cashEttlementCtv.isChecked());
                SPUtils.put(this, KEY_CASH_SETTLEMENT, valueMap.get("online_settlement"));
            }

            if ((distinguishCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_DISTINGUISH, null)).get("buyers_and_sellers_after_weighing"))) {

                ((LinkedTreeMap) valueMap.get("buyers_and_sellers_after_weighing")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("buyers_and_sellers_after_weighing")).put("val", distinguishCtv.isChecked());
                SPUtils.put(this, KEY_DISTINGUISH, valueMap.get("buyers_and_sellers_after_weighing"));
            }
            if ((icCardSettlementCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_ICCARD_SETTLEMENT, null)).get("card_settlement"))) {
                ((LinkedTreeMap) valueMap.get("card_settlement")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("card_settlement")).put("val", icCardSettlementCtv.isChecked());
                SPUtils.put(this, KEY_ICCARD_SETTLEMENT, valueMap.get("card_settlement"));
            }
            if ((stopPrintCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_STOP_PRINT, null)).get("disable_printing"))) {
                ((LinkedTreeMap) valueMap.get("disable_printing")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("disable_printing")).put("val", stopPrintCtv.isChecked());
                SPUtils.put(this, KEY_STOP_PRINT, valueMap.get("disable_printing"));
            }
            if ((noPatchSettlementCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_NO_PATCH_SETTLEMENT, null)).get("allow_batchless_settlement"))) {
                ((LinkedTreeMap) valueMap.get("allow_batchless_settlement")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("allow_batchless_settlement")).put("val", noPatchSettlementCtv.isChecked());
                SPUtils.put(this, KEY_NO_PATCH_SETTLEMENT, valueMap.get("allow_batchless_settlement"));
            }
            if ((autoPrevCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_AUTO_PREV, null)).get("take_a_unit_price"))) {
                ((LinkedTreeMap) valueMap.get("take_a_unit_price")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("take_a_unit_price")).put("val", autoPrevCtv.isChecked());
                SPUtils.put(this, KEY_AUTO_PREV, valueMap.get("take_a_unit_price"));
            }
            if ((cashRoundingCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_CASH_ROUNDING, null)).get("cash_change_rounding"))) {
                ((LinkedTreeMap) valueMap.get("cash_change_rounding")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("cash_change_rounding")).put("val", cashRoundingCtv.isChecked());
                SPUtils.put(this, KEY_CASH_ROUNDING, valueMap.get("cash_change_rounding"));
            }
            if ((stopCashCtv.isChecked() != (Boolean) ((LinkedTreeMap) SPUtils.get(this, KEY_STOP_CASH, null)).get("disable_cash_mode"))) {
                ((LinkedTreeMap) valueMap.get("disable_cash_mode")).put("update_time", System.currentTimeMillis());
                ((LinkedTreeMap) valueMap.get("disable_cash_mode")).put("val", stopCashCtv.isChecked());
                SPUtils.put(this, KEY_STOP_CASH, valueMap.get("disable_cash_mode"));
            }

        }*/
    }

    public void getSettingData() {
        RetrofitFactory.getInstance().API()
                .getSettingData(AccountManager.getInstance().getAdminToken(), Constants.MAC_TEST)
                .compose(this.<BaseEntity>setThread())
                .subscribe(new Observer<BaseEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();

                    }

                    @Override
                    public void onNext(BaseEntity settingDataBeanBaseEntity) {
                        boolean isSu = settingDataBeanBaseEntity.isSuccess();
                        if (settingDataBeanBaseEntity.isSuccess()) {
                            LinkedTreeMap map = (LinkedTreeMap) settingDataBeanBaseEntity.getData();

                            loginTypeList.addAll((Collection<? extends Map<String, String>>) map.get("default_login_type"));
                            pricingModelList.addAll((Collection<? extends Map<String, String>>) map.get("default_pricing_model"));
                            printerList.addAll((Collection<? extends Map<String, String>>) map.get("printer_configuration"));
                            roundingWeightList.addAll((Collection<? extends Map<String, String>>) map.get("rounding_weight"));
                            screenUnitList.addAll((Collection<? extends Map<String, String>>) map.get("screen_unit_display"));
                            balanceRoundingList.addAll((Collection<? extends Map<String, String>>) map.get("balance_rounding"));
                            valueMap = (LinkedTreeMap) ((LinkedTreeMap) settingDataBeanBaseEntity.getData()).get("value");

                            LinkedHashMap saveMap = (LinkedHashMap) SPUtils.readObject(SystemSettingsActivity.this, KEY_DEFAULT_LOGIN_TYPE);
                            if (saveMap != null) {
                                Long loginDate = (Long) saveMap.get("update_time");
                                Long valueDate = ((Double) ((LinkedTreeMap) valueMap.get("default_login_type")).get("update_time")).longValue();
                                if (loginDate.compareTo(valueDate) > 0) {
                                    loginTypeTv.setText((saveMap.get("val")).toString());
                                }
                            }
//                            loginTypeTv.setText(loginTypeList.get(0).get("1"));
                            printerTv.setText(printerList.get(0).get("1"));
                            balanceRoundingTv.setText(balanceRoundingList.get(0).get("1"));
                            priceingMethodTv.setText(pricingModelList.get(0).get("1"));
                            weightRoundingTv.setText(roundingWeightList.get(0).get("1"));
                            weightUnitTv.setText(screenUnitList.get(0).get("1"));


                            LinkedTreeMap priceAfterSaving = (LinkedTreeMap) valueMap.get("price_after_saving");
                            notClearPriceCtv.setChecked((Boolean) priceAfterSaving.get("val"));
                            saveWeightCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("confirm_the_preservation")).get("val"));
                            autoObtainCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("buyers_and_sellers_by_default")).get("val"));
                            cashEttlementCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("online_settlement")).get("val"));
                            distinguishCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("buyers_and_sellers_after_weighing")).get("val"));
                            icCardSettlementCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("card_settlement")).get("val"));
                            stopPrintCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("disable_printing")).get("val"));
                            noPatchSettlementCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("allow_batchless_settlement")).get("val"));
                            autoPrevCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("take_a_unit_price")).get("val"));
                            cashRoundingCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("cash_change_rounding")).get("val"));
                            stopCashCtv.setChecked((Boolean) ((LinkedTreeMap) valueMap.get("disable_cash_mode")).get("val"));

                            buyerNumberTv.setText(((LinkedTreeMap) valueMap.get("default_buyer_number")).get("val") != null ? ((LinkedTreeMap) valueMap.get("default_buyer_number")).get("val").toString() : "");
                            sellerNumberTv.setText(((LinkedTreeMap) valueMap.get("default_seller_number")).get("val") != null ? ((LinkedTreeMap) valueMap.get("default_seller_number")).get("val").toString() : "");

                        } else {
                            showLoading(settingDataBeanBaseEntity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeLoading();
                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });

//                .getSettingData(MacManager.getInstace(this).getMac())

    }
}

