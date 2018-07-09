package com.axecom.iweight.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.SubOrderBean;
import com.axecom.iweight.bean.SubOrderReqBean;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.uiutils.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018-5-15.
 */

public class UseCashActivity extends BaseActivity implements View.OnClickListener{

    private View rootView;
    private Button confirmBtn;
    private Button cancelBtn;
    private Button cashPayBtn;
    private Button aliPayBtn;
    private Button wechatPayBtn;
    private SubOrderReqBean orderBean;
    private LinearLayout cashPayLayout;
    private ImageView qrCodeIv;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.cash_dialog_layout, null);
        confirmBtn = findViewById(R.id.cash_dialog_confirm_btn);
        cancelBtn = findViewById(R.id.cash_dialog_cancel_btn);
        cashPayBtn = findViewById(R.id.cash_dialog_cash_pay_btn);
        aliPayBtn = findViewById(R.id.cash_dialog_alipay_btn);
        wechatPayBtn = findViewById(R.id.cash_dialog_wechat_pay_btn);
        cashPayLayout = findViewById(R.id.cash_dialog_cash_pay_layout);
        qrCodeIv = findViewById(R.id.cash_dialog_qr_code_iv);

        orderBean = (SubOrderReqBean) getIntent().getExtras().getSerializable("orderBean");

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cash_dialog_confirm_btn:
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
                setOrderBean("4");
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
                setOrderBean("2");
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
                setOrderBean("1");
                break;
        }
    }

    public void setOrderBean(String payId){
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
                    public void onNext(BaseEntity<SubOrderBean> subOrderBeanBaseEntity) {
                        if (subOrderBeanBaseEntity.isSuccess()) {
                            imageLoader.displayImage(subOrderBeanBaseEntity.getData().getCode_img_url(), qrCodeIv, options);
                        } else {
                            showLoading(subOrderBeanBaseEntity.getMsg());
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
}
