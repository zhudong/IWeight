package com.axecom.iweight.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.axecom.iweight.R;


/**
 * Created by yuan on 2016/3/3.
 */
public class NumberCounter2 extends LinearLayout implements View.OnClickListener {

    private LinearLayout mRoot;
    private OnCounterLimitListener mListener;
    private OnCounterClickListener mOnCountClickListener;
    private ImageView mMinus;
    public EditText mNumber;
    private ImageView mplus;

    public interface OnCounterLimitListener {
        public void onStartLimit(NumberCounter2 view);

        public void onEndLimit(NumberCounter2 view);
    }

    public interface OnCounterClickListener {
        public void onPlusClick(NumberCounter2 view);

        public void onMinusClick(NumberCounter2 view);
    }

    public NumberCounter2(Context context) {
        this(context, null);
    }

    public NumberCounter2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context content) {
        mRoot = (LinearLayout) View.inflate(content, R.layout.view_number_counter, this);
        mMinus = (ImageView) mRoot.findViewById(R.id.iv_number_minus);
        mNumber = (EditText) mRoot.findViewById(R.id.tv_number_current);
        mplus = (ImageView) mRoot.findViewById(R.id.iv_number_plus);
        mplus.setOnClickListener(this);
        mMinus.setOnClickListener(this);
//        mNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        mNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    mCurrentNumber = Integer.valueOf(s.toString());
                    int value = Integer.parseInt(s.toString());
                    if (value > mMaxValue) {
                        setNumber(mMaxValue);
                    }
                    else if (value < mMinValue) {
                        setNumber(mMinValue);
                    }
                    else {
                        if (value == mCurrentNumber) return;
                        setNumber(value);
                    }
                } else {
                    setNumber(mMinValue);
                  /*  if(!s.toString().equals(""))
                        return;
                    mNumber.setText(mMinValue);*/
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private int mMinValue;
    private int mMaxValue;
    private int mCurrentNumber = mMinValue;

    public void init(int min, int max) {
        if(min>max){
            max = min;
        }
        mMinValue = min;
        mMaxValue = max;
        mCurrentNumber = min;
        setNumber(min);
    }

    public void setOnCounterClickListener(OnCounterClickListener mOnCountClickListener) {
        this.mOnCountClickListener = mOnCountClickListener;
    }

    public void setOnCounterLimitListener(OnCounterLimitListener listener) {
        this.mListener = listener;
    }

    public void setNumber(int number) {
        this.mCurrentNumber = number;
        String text = mCurrentNumber + "";
        mNumber.setText(text);
        mNumber.setSelection(text.length());
    }

    public void setNumberText(int number) {
        this.mCurrentNumber = number;
        mNumber.setText(mCurrentNumber + "");
    }

    public int getCurrentNumber() {
        return mCurrentNumber;
    }


    public void plus() {
        if (mCurrentNumber < mMaxValue) {
            mCurrentNumber = mCurrentNumber + 1;
        }
        mNumber.setText(mCurrentNumber + "");
    }

    public void minus() {
        if (mCurrentNumber > mMinValue) {
            --mCurrentNumber;
            mNumber.setText(mCurrentNumber + "");
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.iv_number_minus == v.getId()) {
            if (mCurrentNumber == mMinValue && mListener != null)
                mListener.onStartLimit(this);
            if (mOnCountClickListener != null && mCurrentNumber > mMinValue){
                minus();
                mOnCountClickListener.onMinusClick(this);
            }
        }
        if (R.id.iv_number_plus == v.getId()) {
            plus();
            if (mCurrentNumber == mMaxValue && mListener != null)
                mListener.onEndLimit(this);

            if (mOnCountClickListener != null)
                mOnCountClickListener.onPlusClick(this);
        }

    }
}
