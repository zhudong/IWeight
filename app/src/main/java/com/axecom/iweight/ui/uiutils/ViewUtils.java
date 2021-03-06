package com.axecom.iweight.ui.uiutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.axecom.iweight.base.BaseActivity;


/**
 * Created by Longer on 2016/10/26.
 */
public class ViewUtils {

    private Activity myActivity;
    private Toast toast = null;

    public ViewUtils(BaseActivity myActivity) {
        this.myActivity = myActivity;
        // MSCTool.NowActivity = myActivity;
    }


    /***
     * Toast出提示信息
     ****/
    public void toast(String text) {
        if (null == toast) {
            toast = Toast.makeText(myActivity, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
        log(text);
    }


    public void log(String key, String info) {
        Log.d(key, info);
    }


    @SuppressLint("NewApi")
    public void log(String info) {
        if (info != null && !info.isEmpty()) {
            log(myActivity.getComponentName().getClassName(), info);
        } else {
            log(myActivity.getComponentName().getClassName(), "info==null或者为空 ");
        }

    }


    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int frameId) {
        if (fragmentManager == null || fragment == null) {
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }
}
