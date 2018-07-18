package com.axecom.iweight.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.base.BusEvent;
import com.axecom.iweight.bean.PayNoticeBean;
import com.axecom.iweight.bean.SubOrderBean;
import com.axecom.iweight.bean.SubOrderReqBean;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.uiutils.ImageLoaderHelper;
import com.axecom.iweight.ui.view.SoftKey;
import com.axecom.iweight.utils.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018-5-15.
 */

public class UseCashActivity extends BaseActivity implements View.OnClickListener {

    private View rootView;
    private Button confirmBtn;
    private Button cancelBtn;
    private Button cashPayBtn;
    private Button aliPayBtn;
    private Button wechatPayBtn;
    private EditText cashEt;
    private TextView priceTotalTv;
    private TextView priceRoundTv;
    private TextView priceChangeTv;
    private SubOrderReqBean orderBean;
    private LinearLayout cashPayLayout;
    private ImageView qrCodeIv;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Intent intent;
    private Bundle bundle;
    private SoftKey softKey;
    private MyRun mRun;
    private String payId;
    private Bitmap bitmap;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.cash_dialog_layout, null);
        confirmBtn = rootView.findViewById(R.id.cash_dialog_confirm_btn);
        cancelBtn = rootView.findViewById(R.id.cash_dialog_cancel_btn);
        cashPayBtn = rootView.findViewById(R.id.cash_dialog_cash_pay_btn);
        aliPayBtn = rootView.findViewById(R.id.cash_dialog_alipay_btn);
        wechatPayBtn = rootView.findViewById(R.id.cash_dialog_wechat_pay_btn);
        cashPayLayout = rootView.findViewById(R.id.cash_dialog_cash_pay_layout);
        qrCodeIv = rootView.findViewById(R.id.cash_dialog_qr_code_iv);
        cashEt = rootView.findViewById(R.id.cash_dialog_cash_et);
        priceTotalTv = rootView.findViewById(R.id.use_cash_price_total_tv);
        priceRoundTv = rootView.findViewById(R.id.use_cash_price_round_tv);
        priceChangeTv = rootView.findViewById(R.id.use_cash_change_tv);
        softKey = rootView.findViewById(R.id.cash_dialog_softkey);
        disableShowInput(cashEt);
        cashEt.requestFocus();

        orderBean = (SubOrderReqBean) getIntent().getExtras().getSerializable("orderBean");
        priceTotalTv.setText(orderBean.getTotal_amount());
        priceRoundTv.setText(new DecimalFormat("#.00").format(Double.parseDouble(orderBean.getTotal_amount())));

        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderHelper.getInstance(this).getDisplayOptions();
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        cashPayBtn.setOnClickListener(this);
        aliPayBtn.setOnClickListener(this);
        wechatPayBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        softKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getAdapter().getItem(position).toString();
                setEditText(cashEt, position, text);
                priceChangeTv.setText(Float.parseFloat(text) - Float.parseFloat(priceRoundTv.getText().toString()) + "");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRun);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cash_dialog_confirm_btn:
                payId = "4";
                setOrderBean(payId);
                break;
            case R.id.cash_dialog_cancel_btn:
                finish();
                break;
            case R.id.cash_dialog_cash_pay_btn:
                cashPayBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_green_bg2));
                cashPayBtn.setTextColor(ContextCompat.getColor(this, R.color.white));
                aliPayBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                aliPayBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                wechatPayBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                wechatPayBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                cashPayLayout.setVisibility(View.VISIBLE);
                qrCodeIv.setVisibility(View.GONE);
                break;
            case R.id.cash_dialog_alipay_btn:
                cashPayBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                cashPayBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                aliPayBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_green_bg2));
                aliPayBtn.setTextColor(ContextCompat.getColor(this, R.color.white));
                wechatPayBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                wechatPayBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                cashPayLayout.setVisibility(View.GONE);
                qrCodeIv.setVisibility(View.VISIBLE);
                payId = "2";
                setOrderBean(payId);
                break;
            case R.id.cash_dialog_wechat_pay_btn:
                cashPayBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                cashPayBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                aliPayBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_white_btn_bg));
                aliPayBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                wechatPayBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_green_bg2));
                wechatPayBtn.setTextColor(ContextCompat.getColor(this, R.color.white));
                cashPayLayout.setVisibility(View.GONE);
                qrCodeIv.setVisibility(View.VISIBLE);
                payId = "1";
                setOrderBean(payId);
                break;
        }
    }

    public void setOrderBean(String payId) {
        orderBean.setPayment_id(payId);
        submitOrder(orderBean);
    }

    public void submitOrder(SubOrderReqBean subOrderReqBean) {
        RetrofitFactory.getInstance().API()
                .submitOrder(subOrderReqBean)
                .compose(this.<BaseEntity<SubOrderBean>>setThread())
                .subscribe(new Observer<BaseEntity<SubOrderBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(final BaseEntity<SubOrderBean> subOrderBeanBaseEntity) {
                        if (subOrderBeanBaseEntity.isSuccess()) {
                            imageLoader.displayImage(subOrderBeanBaseEntity.getData().getCode_img_url(), qrCodeIv, options);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        bitmap = BitmapFactory.decodeStream(new URL(subOrderBeanBaseEntity.getData().getPrint_code_img()).openStream());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            mRun = new MyRun(subOrderBeanBaseEntity);
                            mHandler.postDelayed(mRun, 1000);
                        } else {
                            showLoading(subOrderBeanBaseEntity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeLoading();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });
    }

    class MyRun implements Runnable {

        private BaseEntity<SubOrderBean> subOrderBeanBaseEntity;

        public MyRun(BaseEntity<SubOrderBean> subOrderBeanBaseEntity) {
            this.subOrderBeanBaseEntity = subOrderBeanBaseEntity;
        }

        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.obj = subOrderBeanBaseEntity.getData();
            mHandler.sendMessage(msg);
            mHandler.postDelayed(this, 1000 * 3);//延迟5秒,再次执行task本身,实现了循环的效果
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SubOrderBean subOrderBeanBaseEntity = (SubOrderBean) msg.obj;

            String order_no = subOrderBeanBaseEntity.getOrder_no();
            String qrCode =subOrderBeanBaseEntity.getPrint_code_img();
            getPayNotice(order_no, qrCode);
        }
    };

    public void getPayNotice(final String order_no, final String qrCode) {
        RetrofitFactory.getInstance().API()
                .getPayNotice(order_no)
                .compose(this.<BaseEntity<PayNoticeBean>>setThread())
                .subscribe(new Observer<BaseEntity<PayNoticeBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseEntity<PayNoticeBean> payNoticeBeanBaseEntity) {
                        if (payNoticeBeanBaseEntity.isSuccess()) {
                            if (payNoticeBeanBaseEntity.getData().flag == 0) {
                                showLoading(payNoticeBeanBaseEntity.getData().msg);
                                mHandler.removeCallbacks(mRun);
                                EventBus.getDefault().post(new BusEvent(BusEvent.PRINTER_LABEL, bitmap, order_no, payId, qrCode));
                            }
                            LogUtils.d(payNoticeBeanBaseEntity.getData().msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
