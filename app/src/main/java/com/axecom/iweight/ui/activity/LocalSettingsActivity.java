package com.axecom.iweight.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.bean.ChooseBean;
import com.axecom.iweight.ui.view.ChooseDialog;
import com.axecom.iweight.ui.view.SoftKeyborad;

import java.util.ArrayList;
import java.util.List;

public class LocalSettingsActivity extends BaseActivity {

    private View rootView;

    private Button weightPortBtn, printerPortBtn, ledPortBtn, readCardPortBtn, readCardTypeBtn, saveBtn, backBtn;
    private TextView weightPortTv, printerPortTv, printerCountTv, transactionDataTv, baudRateTv, serverIPTv, dataDaysTv, sleepTimeTv, serverPortTv;
    private TextView weightPortChooseTv, printerPortChooseTv, ledPortChooseTv, readCardPortChooseTv, readCardTypeChooseTv;

    private List<ChooseBean> list;
    private int weightPos = 0,printerPos = 0,ledPos = 0,readCardPortPos = 0,readCardTypePos = 0;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.local_settings_activity, null);

        weightPortBtn = rootView.findViewById(R.id.local_settings_weight_port_choose_btn);
        printerPortBtn = rootView.findViewById(R.id.local_settings_printer_port_choose_btn);
        ledPortBtn = rootView.findViewById(R.id.local_settings_led_port_choose_btn);
        readCardPortBtn = rootView.findViewById(R.id.local_settings_read_card_port_choose_btn);
        readCardTypeBtn = rootView.findViewById(R.id.local_settings_read_card_type_choose_btn);
        saveBtn = rootView.findViewById(R.id.local_settings_save_btn);
        backBtn = rootView.findViewById(R.id.local_settings_back_btn);

        weightPortTv = rootView.findViewById(R.id.local_settings_weight_port_tv);
        printerPortTv = rootView.findViewById(R.id.local_settings_printer_port_tv);
        printerCountTv = rootView.findViewById(R.id.local_settings_printer_count_tv);
        transactionDataTv = rootView.findViewById(R.id.local_settings_transaction_data_tv);
        baudRateTv = rootView.findViewById(R.id.local_settings_weight_baudrate_tv);
        serverIPTv = rootView.findViewById(R.id.local_settings_server_ip_tv);
        dataDaysTv = rootView.findViewById(R.id.local_settings_data_days_tv);
        sleepTimeTv = rootView.findViewById(R.id.local_settings_sleep_time_tv);
        serverPortTv = rootView.findViewById(R.id.local_settings_server_port_tv);

        weightPortChooseTv = rootView.findViewById(R.id.local_settings_weight_port_choose_tv);
        printerPortChooseTv = rootView.findViewById(R.id.local_settings_printer_port_choose_tv);
        ledPortChooseTv = rootView.findViewById(R.id.local_settings_led_port_choose_tv);
        readCardPortChooseTv = rootView.findViewById(R.id.local_settings_read_card_port_choose_tv);
        readCardTypeChooseTv = rootView.findViewById(R.id.local_settings_read_card_type_choose_tv);

        weightPortBtn.setOnClickListener(this);
        printerPortBtn.setOnClickListener(this);
        ledPortBtn.setOnClickListener(this);
        readCardPortBtn.setOnClickListener(this);
        readCardTypeBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        weightPortTv.setOnClickListener(this);
        printerPortTv.setOnClickListener(this);
        printerCountTv.setOnClickListener(this);
        transactionDataTv.setOnClickListener(this);
        baudRateTv.setOnClickListener(this);
        serverIPTv.setOnClickListener(this);
        dataDaysTv.setOnClickListener(this);
        sleepTimeTv.setOnClickListener(this);
        serverPortTv.setOnClickListener(this);

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
            case R.id.local_settings_weight_port_choose_btn:
//                chooseBuilder.create(list,weightPos, new ChooseDialog.OnSelectedListener() {
//                    @Override
//                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
//                        weightPos = position;
//                        weightPortChooseTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
//                    }
//                }).show();
                break;
            case R.id.local_settings_printer_port_choose_btn:
//                chooseBuilder.create(list, printerPos, new ChooseDialog.OnSelectedListener() {
//                    @Override
//                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
//                        printerPos = position;
//                        printerPortChooseTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
//                    }
//                }).show();
                break;
            case R.id.local_settings_led_port_choose_btn:
//                chooseBuilder.create(list, ledPos, new ChooseDialog.OnSelectedListener() {
//                    @Override
//                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
//                        ledPos = position;
//                        ledPortChooseTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
//                    }
//                }).show();
                break;
            case R.id.local_settings_read_card_port_choose_btn:
//                chooseBuilder.create(list, readCardPortPos, new ChooseDialog.OnSelectedListener() {
//                    @Override
//                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
//                        readCardPortPos = position;
//                        readCardPortChooseTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
//                    }
//                }).show();
                break;
            case R.id.local_settings_read_card_type_choose_btn:
//                chooseBuilder.create(list, readCardTypePos, new ChooseDialog.OnSelectedListener() {
//                    @Override
//                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
//                        readCardTypePos = position;
//                        readCardTypeChooseTv.setText(((ChooseBean)parent.getAdapter().getItem(position)).getChooseItem());
//                    }
//                }).show();
                break;
            case R.id.local_settings_save_btn:
                break;
            case R.id.local_settings_back_btn:
                finish();
                break;
            case R.id.local_settings_weight_port_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        weightPortTv.setText(result);
                    }
                }).show();
                break;
            case R.id.local_settings_printer_port_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        printerPortTv.setText(result);
                    }
                }).show();
                break;
            case R.id.local_settings_printer_count_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        printerCountTv.setText(result);
                    }
                }).show();
                break;
            case R.id.local_settings_transaction_data_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        transactionDataTv.setText(result);
                    }
                }).show();
                break;
            case R.id.local_settings_weight_baudrate_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        baudRateTv.setText(result);
                    }
                }).show();
                break;
            case R.id.local_settings_server_ip_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        serverIPTv.setText(result);
                    }
                }).show();
                break;
            case R.id.local_settings_data_days_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        dataDaysTv.setText(result);
                    }
                }).show();
                break;
            case R.id.local_settings_sleep_time_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        sleepTimeTv.setText(result);
                    }
                }).show();
                break;
            case R.id.local_settings_server_port_tv:
                softBuilder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        serverPortTv.setText(result);
                    }
                }).show();
                break;

        }
    }
}
