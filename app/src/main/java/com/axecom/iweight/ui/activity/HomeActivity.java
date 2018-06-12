package com.axecom.iweight.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.LoginData;
import com.axecom.iweight.bean.WeightBean;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.manager.MacManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.view.SoftKeyborad;
import com.axecom.iweight.utils.LogUtils;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018-5-8.
 */

public class HomeActivity extends BaseActivity {

    private View rootView;
    private Button confirmBtn;
    private TextView cardNumberTv;
    private TextView pwdTv;
    private TextView loginTv;
    private TextView weightTv;
    private int weightId;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        confirmBtn = rootView.findViewById(R.id.home_confirm_btn);
        cardNumberTv = rootView.findViewById(R.id.home_card_number_tv);
        pwdTv = rootView.findViewById(R.id.home_pwd_tv);
        loginTv = rootView.findViewById(R.id.home_login_tv);
        weightTv = rootView.findViewById(R.id.home_weight_number_tv);

        pwdTv.setOnClickListener(this);
        loginTv.setOnClickListener(this);
        cardNumberTv.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
//        getScalesIdByMac(MacManager.getInstace(HomeActivity.this).getMac());
        getScalesIdByMac("84:73:03:5b:ba:bb");
    }

    @Override
    public void onClick(View v) {
        SoftKeyborad.Builder builder = new SoftKeyborad.Builder(HomeActivity.this);
        switch (v.getId()){
            case R.id.home_login_tv:
            case R.id.home_confirm_btn:
//                getScalesIdByMac(MacManager.getInstace(HomeActivity.this).getMac());
                clientLogin(weightId + "", cardNumberTv.getText().toString(), pwdTv.getText().toString());
//                clientLogin("4", cardNumberTv.getText().toString(), pwdTv.getText().toString());
                break;
            case R.id.home_card_number_tv:
                builder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        cardNumberTv.setText(result);
                    }
                }).show();
                break;
            case R.id.home_pwd_tv:
                builder.create(new SoftKeyborad.OnConfirmedListener() {
                    @Override
                    public void onConfirmed(String result) {
                        pwdTv.setText(result);
                    }
                }).show();
                break;
        }
    }

    public void getScalesIdByMac(String mac){
        showLoading();
        RetrofitFactory.getInstance().API()
                .getScalesIdByMac(mac)
                .compose(this.<BaseEntity<WeightBean>>setThread())
                .subscribe(new Observer<BaseEntity<WeightBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(final BaseEntity<WeightBean> baseEntity) {
                        if(baseEntity.isSuccess()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    weightId = baseEntity.getData().getId();
                                    weightTv.setText(weightId + "");
                                    AccountManager.getInstance().saveScalesId(weightId + "");
                                    isOnline();
                                }
                            });
                        }
                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
                        closeLoading();

                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });
    }

    public void clientLogin(String scalesId, String serialNumber, String password){
        showLoading();
        RetrofitFactory.getInstance().API()
                .clientLogin(scalesId, serialNumber, password)
                .compose(this.<BaseEntity<LoginData>>setThread())
                .subscribe(new Observer<BaseEntity<LoginData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseEntity<LoginData> loginDataBaseEntity) {
                        if(loginDataBaseEntity.isSuccess()){
                            AccountManager.getInstance().saveToken(loginDataBaseEntity.getData().getToken());
                            startDDMActivity(MainActivity.class, false);
                        }else {
                            showLoading(loginDataBaseEntity.getMsg());
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
    }

    public void isOnline(){
        RetrofitFactory.getInstance().API()
                .isOnline(AccountManager.getInstance().getScalesId())
                .compose(this.<BaseEntity>setThread())
                .subscribe(new Observer<BaseEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseEntity baseEntity) {
                        if(baseEntity.isSuccess()){

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
