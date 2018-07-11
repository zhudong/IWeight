package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.ChooseBean;
import com.axecom.iweight.bean.LocalSettingsBean;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.view.ChooseDialog;
import com.axecom.iweight.ui.view.ChooseDialog2;
import com.axecom.iweight.ui.view.SoftKeyborad;
import com.axecom.iweight.utils.LogUtils;
import com.axecom.iweight.utils.SPUtils;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LocalSettingsActivity extends BaseActivity {

    public static final String KEY_PRINTER_COUNT  = "printerCountTv";

    private View rootView;

    private Button weightPortBtn, printerPortBtn, ledPortBtn, readCardPortBtn, readCardTypeBtn, saveBtn, backBtn;
    private TextView weightPortTv, printerPortTv, printerCountTv, transactionDataTv, baudRateTv, serverIPTv, dataDaysTv, sleepTimeTv, serverPortTv;
    private TextView weightPortChooseTv, printerPortChooseTv, ledPortChooseTv, readCardPortChooseTv, readCardTypeChooseTv;

    private List<LocalSettingsBean.CardReaderTypeList> cardReaderTypeList;
    private List<LocalSettingsBean.WeightPort> weightPorts;
    private List<LocalSettingsBean.PrinterPort> printerPorts;
    private List<LocalSettingsBean.ExternalLedPort> externalLedPorts;
    private List<LocalSettingsBean.CardReaderPort> cardReaderPorts;
    private int weightPos = 0, printerPos = 0, ledPos = 0, readCardPortPos = 0, readCardTypePos = 0;

    private List<UsbSerialDriver> availableDrivers;
    private UsbManager manager;
    private ArrayList<ChooseBean> deviceList;

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
        getUsbDevices();
        getScalesSettingData();
        cardReaderTypeList = new ArrayList<>();
        weightPorts = new ArrayList<>();
        printerPorts = new ArrayList<>();
        externalLedPorts = new ArrayList<>();
        cardReaderPorts = new ArrayList<>();
//        ChooseBean bean;
//        for (int i = 0; i < 4; i++) {
//            bean= new ChooseBean();
//            bean.setChecked(false);
//            bean.setChooseItem("测试数据 " + i);
//            list.add(bean);
//        }
    }

    public void getUsbDevices(){
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }
        ChooseBean chooseBean;
        deviceList = new ArrayList();
        for (int i = 0; i < availableDrivers.size(); i++) {
            chooseBean = new ChooseBean();
            chooseBean.setChooseItem(availableDrivers.get(i).getDevice().getDeviceName());
            deviceList.add(chooseBean);
        }
    }

    public void getScalesSettingData() {
        RetrofitFactory.getInstance().API()
                .getScalesSettingData(AccountManager.getInstance().getAdminToken(), Constants.MAC_TEST)
                .compose(this.<BaseEntity<LocalSettingsBean>>setThread())
                .subscribe(new Observer<BaseEntity<LocalSettingsBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(BaseEntity<LocalSettingsBean> localSettingsBeanBaseEntity) {
                        if (localSettingsBeanBaseEntity.isSuccess()) {
                            Long saveDate = (Long) SPUtils.get(LocalSettingsActivity.this, "currentDate", null);
                            if(saveDate !=  null){
                                if (saveDate.compareTo(Long.parseLong(localSettingsBeanBaseEntity.getData().value.card_reader_type.update_time)) > 0){
                                    LogUtils.d("111111111111111111111");
                                }
                            }
                            cardReaderTypeList.addAll(localSettingsBeanBaseEntity.getData().card_reader_type_list);
                            weightPorts.addAll(localSettingsBeanBaseEntity.getData().weight_port);
                            printerPorts.addAll(localSettingsBeanBaseEntity.getData().printer_port);
                            externalLedPorts.addAll(localSettingsBeanBaseEntity.getData().external_led_port);
                            cardReaderPorts.addAll(localSettingsBeanBaseEntity.getData().card_reader_port);

                            weightPortChooseTv.setText(((LocalSettingsBean.WeightPort)localSettingsBeanBaseEntity.getData().weight_port.get(0)).val);
                            String printerProt = SPUtils.getString(LocalSettingsActivity.this, printerPortChooseTv.getId() + "", "");
                            String readCardPort = SPUtils.getString(LocalSettingsActivity.this, readCardPortChooseTv.getId() + "", "");
                            String printerCount = SPUtils.getString(LocalSettingsActivity.this, KEY_PRINTER_COUNT, "");
                            String dataDays = SPUtils.getString(LocalSettingsActivity.this, dataDaysTv.getId() + "", "");
                            String transactionData = SPUtils.getString(LocalSettingsActivity.this, transactionDataTv.getId() + "", "");
                            if(!TextUtils.isEmpty(printerProt)){
                                printerPortChooseTv.setText(printerProt);
                            }else {
                                printerPortChooseTv.setText(((LocalSettingsBean.PrinterPort)localSettingsBeanBaseEntity.getData().printer_port.get(0)).val);
                            }
                            if(!TextUtils.isEmpty(readCardPort)){
                                readCardPortChooseTv.setText(readCardPort);
                            }else {
                                readCardPortChooseTv.setText(((LocalSettingsBean.CardReaderPort)localSettingsBeanBaseEntity.getData().card_reader_port.get(0)).val);
                            }

                            if(!TextUtils.isEmpty(printerCount)){
                                printerCountTv.setText(printerCount);
                            }else {
                                printerCountTv.setText(((LocalSettingsBean.CardReaderPort)localSettingsBeanBaseEntity.getData().card_reader_port.get(0)).val);
                            }
                            if(!TextUtils.isEmpty(dataDays)){
                                dataDaysTv.setText(dataDays);
                            }else {
                                dataDaysTv.setText(((LocalSettingsBean.CardReaderPort)localSettingsBeanBaseEntity.getData().card_reader_port.get(0)).val);
                            }
                            if(!TextUtils.isEmpty(transactionData)){
                                transactionDataTv.setText(transactionData);
                            }else {
                                transactionDataTv.setText(((LocalSettingsBean.CardReaderPort)localSettingsBeanBaseEntity.getData().card_reader_port.get(0)).val);
                            }

                            ledPortChooseTv.setText(((LocalSettingsBean.ExternalLedPort)localSettingsBeanBaseEntity.getData().external_led_port.get(0)).val);
                            readCardTypeChooseTv.setText(((LocalSettingsBean.CardReaderTypeList)localSettingsBeanBaseEntity.getData().card_reader_type_list.get(0)).val);

                            weightPortTv.setText(localSettingsBeanBaseEntity.getData().value.weight_port.val);
                            printerPortTv.setText(localSettingsBeanBaseEntity.getData().value.printer_port.val);
                            printerCountTv.setText(localSettingsBeanBaseEntity.getData().value.number_of_prints_configuration.val);
                            transactionDataTv.setText(localSettingsBeanBaseEntity.getData().value.clear_transaction_data.val);
                            baudRateTv.setText(localSettingsBeanBaseEntity.getData().value.weighing_plate_baud_rate.val);
                            serverIPTv.setText(localSettingsBeanBaseEntity.getData().value.server_ip.val);
                            dataDaysTv.setText(localSettingsBeanBaseEntity.getData().value.lot_validity_time.val);
                            sleepTimeTv.setText(localSettingsBeanBaseEntity.getData().value.screen_off.val);
                            serverPortTv.setText(localSettingsBeanBaseEntity.getData().value.server_port.val);
                        } else {
                            showLoading(localSettingsBeanBaseEntity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        closeLoading();
                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });
    }


    public void saveSettingsToSP(){
        SPUtils.put(this, "currentDate", System.currentTimeMillis());
        SPUtils.putString(this, printerPortChooseTv.getId() + "", printerPortChooseTv.getText().toString());
        SPUtils.put(this, KEY_PRINTER_COUNT, Integer.parseInt(printerCountTv.getText().toString()));
        SPUtils.putString(this, transactionDataTv.getId() + "", transactionDataTv.getText().toString());
        SPUtils.putString(this, baudRateTv.getId() + "", baudRateTv.getText().toString());
        SPUtils.putString(this, serverIPTv.getId() + "", serverIPTv.getText().toString());
        SPUtils.putString(this, ledPortChooseTv.getId() + "", ledPortChooseTv.getText().toString());
        SPUtils.putString(this, readCardPortChooseTv.getId() + "", readCardPortChooseTv.getText().toString());
        SPUtils.putString(this, dataDaysTv.getId() + "", dataDaysTv.getText().toString());
        SPUtils.putString(this, sleepTimeTv.getId() + "", sleepTimeTv.getText().toString());
        SPUtils.putString(this, readCardTypeChooseTv.getId() + "", readCardTypeChooseTv.getText().toString());
        SPUtils.putString(this, serverPortTv.getId() + "", serverPortTv.getText().toString());
    }

    @Override
    public void onClick(View v) {
        ChooseDialog2.Builder chooseBuilder = new ChooseDialog2.Builder(this);
        SoftKeyborad.Builder softBuilder = new SoftKeyborad.Builder(this);
        switch (v.getId()) {
            case R.id.local_settings_weight_port_choose_btn:
                chooseBuilder.create(deviceList, weightPos, new ChooseDialog2.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        weightPos = position;
                        weightPortChooseTv.setText(((ChooseBean) parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.local_settings_printer_port_choose_btn:
                chooseBuilder.create(deviceList, printerPos, new ChooseDialog2.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        printerPos = position;
                        printerPortChooseTv.setText(((ChooseBean) parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.local_settings_led_port_choose_btn:
                ChooseBean externalLed;
                ArrayList<ChooseBean> externalLedList = new ArrayList();
                for (int i = 0; i < externalLedPorts.size(); i++) {
                    externalLed = new ChooseBean();
                    externalLed.setChooseItem(externalLedPorts.get(i).val);
                    externalLedList.add(externalLed);
                }
                chooseBuilder.create(externalLedList, ledPos, new ChooseDialog2.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        ledPos = position;
                        ledPortChooseTv.setText(((ChooseBean) parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.local_settings_read_card_port_choose_btn:
                chooseBuilder.create(deviceList, readCardPortPos, new ChooseDialog2.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        readCardPortPos = position;
                        readCardPortChooseTv.setText(((ChooseBean) parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.local_settings_read_card_type_choose_btn:
                ChooseBean cardReaderType;
                ArrayList<ChooseBean> cardTypeList = new ArrayList();
                for (int i = 0; i < cardReaderTypeList.size(); i++) {
                    cardReaderType = new ChooseBean();
                    cardReaderType.setChooseItem(cardReaderTypeList.get(i).val);
                    cardTypeList.add(cardReaderType);
                }
                chooseBuilder.create(cardTypeList, readCardTypePos, new ChooseDialog2.OnSelectedListener() {
                    @Override
                    public void onSelected(AdapterView<?> parent, View view, int position, long id) {
                        readCardTypePos = position;
                        readCardTypeChooseTv.setText(((ChooseBean) parent.getAdapter().getItem(position)).getChooseItem());
                    }
                }).show();
                break;
            case R.id.local_settings_save_btn:
                saveSettingsToSP();
                finish();
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
