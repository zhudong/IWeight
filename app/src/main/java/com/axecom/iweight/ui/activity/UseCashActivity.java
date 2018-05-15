package com.axecom.iweight.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.axecom.iweight.R;

/**
 * Created by Administrator on 2018-5-15.
 */

public class UseCashActivity extends Activity implements View.OnClickListener{

    private Button confirmBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_dialog_layout);
        confirmBtn = findViewById(R.id.cash_dialog_confirm_btn);
        cancelBtn = findViewById(R.id.cash_dialog_cancel_btn);

        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cash_dialog_confirm_btn:
            case R.id.cash_dialog_cancel_btn:
                finish();
                break;
        }
    }
}
