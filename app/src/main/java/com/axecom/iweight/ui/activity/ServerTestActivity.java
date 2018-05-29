package com.axecom.iweight.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;

/**
 * Created by Administrator on 2018-5-29.
 */

public class ServerTestActivity extends BaseActivity {

    private View rootView;
    private Button cancelBtn;
    private TextView titleTv;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.server_test_activity, null);
        cancelBtn = rootView.findViewById(R.id.server_test_cancel_btn);
        titleTv = rootView.findViewById(R.id.server_test_title_tv);

        cancelBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.server_test_cancel_btn:
                finish();
                break;
        }
    }
}
