package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.CalibrationBean;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.net.RetrofitFactory;
import com.axecom.iweight.ui.adapter.DigitalAdapter;
import com.axecom.iweight.ui.view.SoftKeyborad;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018-5-29.
 */

public class CalibrationActivity extends BaseActivity {
    private static final String[] DATA_DIGITAL = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "删除", "0", "."};

    private View rootView;
    private TextView firstStepTv;
    private TextView secondStepTv;
    private TextView standardWeighingTv;
    private EditText maxAngeEt;
    private EditText dividingValueEt;
    private EditText calibrationValueEt;
    private Button backBtn;
    private Button nextStepBtn;
    private Button doneBtn;
    private GridView digitalGridView;
    private KeyBoardAdapter keyBoardAdapter;
    private LinearLayout firstLayout;
    private LinearLayout secondLayout;
    private LinearLayout nextStepLayout;
    private ImageView nextStepLineIv;
    private boolean isShowStaffLogin = true;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.calibration_activity, null);
        firstStepTv = rootView.findViewById(R.id.calibration_first_step_tv);
        secondStepTv = rootView.findViewById(R.id.calibration_second_step_tv);
        backBtn = rootView.findViewById(R.id.calibration_back_btn);
        nextStepBtn = rootView.findViewById(R.id.calibration_next_step_btn);
        doneBtn = rootView.findViewById(R.id.calibration_done_btn);
        digitalGridView = rootView.findViewById(R.id.calibration_digital_keys_grid_view);
        firstLayout = rootView.findViewById(R.id.calibration_first_layout);
        secondLayout = rootView.findViewById(R.id.calibration_second_layout);
        standardWeighingTv = rootView.findViewById(R.id.calibration_standard_weighing_tv);
        maxAngeEt = rootView.findViewById(R.id.calibration_max_ange_et);
        maxAngeEt.requestFocus();
        dividingValueEt = rootView.findViewById(R.id.calibration_dividing_value_et);
        calibrationValueEt = rootView.findViewById(R.id.calibration_dcalibration_value_et);
        nextStepLineIv = rootView.findViewById(R.id.calibration_next_step_line_iv);
        nextStepLayout = rootView.findViewById(R.id.calibration_next_step_layout);


        disableShowInput(maxAngeEt);
        disableShowInput(dividingValueEt);
        disableShowInput(calibrationValueEt);
        firstStepTv.setOnClickListener(this);
        secondStepTv.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        nextStepBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(this);
        return rootView;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initView() {
//        Intent intent = new Intent();
//        intent.setClass(this, StaffMemberLoginActivity.class);
//        startActivityForResult(intent, 101);
        List<String> digitaList = new ArrayList<>();

        for (int i = 0; i < DATA_DIGITAL.length; i++) {
            digitaList.add(DATA_DIGITAL[i]);
        }
        keyBoardAdapter = new KeyBoardAdapter(this, DATA_DIGITAL);
        digitalGridView.setAdapter(keyBoardAdapter);
        digitalGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getAdapter().getItem(position).toString();
                if (rootView.findFocus() != null) {
                    switch (rootView.findFocus().getId()) {
                        case R.id.calibration_max_ange_et:
                            setEditText(maxAngeEt, position, item);
                            break;
                        case R.id.calibration_dividing_value_et:
                            setEditText(dividingValueEt, position, item);
                            break;
                        case R.id.calibration_dcalibration_value_et:
                            setEditText(calibrationValueEt, position, item);
                            break;
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calibration_first_step_tv:
                firstStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.white));
                secondStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.black));
                firstStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.green_arrow_right));
                secondStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.white_arrow_right));
                firstLayout.setVisibility(View.VISIBLE);
                secondLayout.setVisibility(View.GONE);
                nextStepLineIv.setVisibility(View.VISIBLE);
                nextStepLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.calibration_second_step_tv:
                secondStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.white));
                firstStepTv.setTextColor(ContextCompat.getColor(CalibrationActivity.this, R.color.black));
                firstStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.white_arrow_right));
                secondStepTv.setBackground(ContextCompat.getDrawable(CalibrationActivity.this, R.drawable.green_arrow_right));
                firstLayout.setVisibility(View.GONE);
                secondLayout.setVisibility(View.VISIBLE);
                nextStepLineIv.setVisibility(View.GONE);
                nextStepLayout.setVisibility(View.GONE);
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
                nextStepLineIv.setVisibility(View.GONE);
                nextStepLayout.setVisibility(View.GONE);
                break;
            case R.id.calibration_done_btn:
                CalibrationBean bean = new CalibrationBean();
                bean.setAdminToken(AccountManager.getInstance().getAdminToken());
                bean.setMax_ange(maxAngeEt.getText().toString());
                bean.setCalibration_value(calibrationValueEt.getText().toString());
                bean.setDividing_value(dividingValueEt.getText().toString());
                bean.setStandard_weighing(standardWeighingTv.getText().toString());
                bean.setScales_id(AccountManager.getInstance().getScalesId());
                storeCalibrationRecord(bean);
                break;
        }

    }

    public void storeCalibrationRecord(CalibrationBean calibrationBean) {
        RetrofitFactory.getInstance().API()
                .storeCalibrationRecord(calibrationBean)
                .compose(this.<BaseEntity>setThread())
                .subscribe(new Observer<BaseEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();

                    }

                    @Override
                    public void onNext(BaseEntity baseEntity) {
                        if (baseEntity.isSuccess()) {
                        } else {
                        showLoading(baseEntity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeLoading();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeLoading();
                            }
                        });
                    }
                });

    }

    class KeyBoardAdapter extends BaseAdapter {
        private Context context;
        private String[] digitals;

        public KeyBoardAdapter(Context context, String[] digitals) {
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
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.calibration_keyborad_item, null);
                holder = new ViewHolder();
                holder.keyBtn = convertView.findViewById(R.id.keyboard_btn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.keyBtn.setText(digitals[position]);
            return convertView;
        }
    }

    class ViewHolder {
        Button keyBtn;
    }
}
