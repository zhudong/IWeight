package com.axecom.iweight.ui.activity;

import android.accounts.Account;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.LoginData;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.view.SoftKey;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class StaffMemberLoginActivity extends BaseActivity {

    private View rootView;
    private EditText numberEt, pwdEt;
    private SoftKey softKey;
    private Button doneBtn, backBtn;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.staff_member_login_activity, null);
        numberEt = rootView.findViewById(R.id.staff_member_number_et);
        disableShowInput(numberEt);
        numberEt.requestFocus();
        pwdEt = rootView.findViewById(R.id.staff_member_pwd_et);
        disableShowInput(pwdEt);
        softKey = rootView.findViewById(R.id.staff_member_softkey);
        doneBtn = rootView.findViewById(R.id.staff_member_done_btn);
        backBtn = rootView.findViewById(R.id.staff_member_back_btn);



        doneBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        return rootView;
    }



    @Override
    public void initView() {
        softKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getAdapter().getItem(position).toString();
                switch (rootView.findFocus().getId()){
                    case R.id.staff_member_number_et:
                        setEditText(numberEt, position, text);
                        break;
                    case R.id.staff_member_pwd_et:
                        setEditText(pwdEt, position, text);
                        break;

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.staff_member_done_btn:
                staffMemberLogin(AccountManager.getInstance().getScalesId(), numberEt.getText().toString(), pwdEt.getText().toString());
                break;
            case R.id.staff_member_back_btn:
                setResult(RESULT_CANCELED);
                finish();
                break;

        }
    }

    public void staffMemberLogin(String scalesId, String serialNumber, String password){
        RetrofitFactory.getInstance().API()
                .staffMemberLogin(scalesId, serialNumber, password)
                .compose(this.<BaseEntity<LoginData>>setThread())
                .subscribe(new Observer<BaseEntity<LoginData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();

                    }

                    @Override
                    public void onNext(BaseEntity<LoginData> loginDataBaseEntity) {
                        if(loginDataBaseEntity.isSuccess()){
                            AccountManager.getInstance().setAdminToken(loginDataBaseEntity.getData().getAdminToken());
                            setResult(RESULT_OK);
                            finish();
                        }else {
                            showLoading(loginDataBaseEntity.getMsg());
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
