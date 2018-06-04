package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.ui.adapter.DigitalAdapter;
import com.axecom.iweight.ui.view.SoftKeyborad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-5-29.
 */

public class CalibrationActivity extends BaseActivity {
    private static final String[] DATA_DIGITAL = {"1","2","3","4","5","6","7","8","9","删除","0","."};

    private View rootView;
    private TextView firstStepTv;
    private TextView secondStepTv;
    private Button backBtn;
    private Button nextStepBtn;
    private GridView digitalGridView;
    private KeyBoardAdapter keyBoardAdapter;
    private LinearLayout firstLayout;
    private LinearLayout secondLayout;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.calibration_activity, null);
        firstStepTv = rootView.findViewById(R.id.calibration_first_step_tv);
        secondStepTv = rootView.findViewById(R.id.calibration_second_step_tv);
        backBtn = rootView.findViewById(R.id.calibration_back_btn);
        nextStepBtn = rootView.findViewById(R.id.calibration_next_step_btn);
        digitalGridView = rootView.findViewById(R.id.calibration_digital_keys_grid_view);
        firstLayout = rootView.findViewById(R.id.calibration_first_layout);
        secondLayout = rootView.findViewById(R.id.calibration_second_layout);

        firstStepTv.setOnClickListener(this);
        secondStepTv.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        nextStepBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        List<String> digitaList = new ArrayList<>();

        for (int i = 0; i < DATA_DIGITAL.length; i++) {
            digitaList.add(DATA_DIGITAL[i]);
        }
        keyBoardAdapter = new KeyBoardAdapter(this, DATA_DIGITAL);
        digitalGridView.setAdapter(keyBoardAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.calibration_first_step_tv:
                firstStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.white));
                secondStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.black));
                firstStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.green_arrow_right));
                secondStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.white_arrow_right));
                firstLayout.setVisibility(View.VISIBLE);
                secondLayout.setVisibility(View.GONE);
                break;
            case R.id.calibration_second_step_tv:
                secondStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.white));
                firstStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.black));
                firstStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.white_arrow_right));
                secondStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.green_arrow_right));
                firstLayout.setVisibility(View.GONE);
                secondLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.calibration_back_btn:
                finish();
                break;
            case R.id.calibration_next_step_btn:
                secondStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.white));
                firstStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.black));
                firstStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.white_arrow_right));
                secondStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.green_arrow_right));
                firstLayout.setVisibility(View.GONE);
                secondLayout.setVisibility(View.VISIBLE);
                break;

        }

    }

    class KeyBoardAdapter extends BaseAdapter {
        private Context context;
        private String[] digitals;

        public KeyBoardAdapter(Context context, String[] digitals){
            this.context = context;
            this.digitals = digitals;
        }

        @Override
        public int getCount() {
            return digitals.length;
        }

        @Override
        public Object getItem(int position) {
            return digitals[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.calibration_keyborad_item, null);
                holder = new ViewHolder();
                holder.keyBtn = convertView.findViewById(R.id.keyboard_btn);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.keyBtn.setText(digitals[position]);
            return convertView;
        }
    }

    class ViewHolder{
        Button keyBtn;
    }
}
