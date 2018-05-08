package com.axecom.iweight.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;

/**
 * Created by Administrator on 2018-5-8.
 */

public class HomeActivity extends BaseActivity {

    private View rootView;
    private Button confirmBtn;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        confirmBtn = rootView.findViewById(R.id.home_confirm_btn);

        confirmBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        startDDMActivity(MainActivity.class, false);
    }
}
