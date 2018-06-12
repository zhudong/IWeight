package com.axecom.iweight.manager;

import android.content.Context;

import com.axecom.iweight.base.SysApplication;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.utils.SPUtils;

public class AccountManager {
    private static AccountManager accountManager = new AccountManager();

    private String token;
    private String scalesId;
    private static Context mCtx;

    private AccountManager(){}

    public static AccountManager getInstance(){
        mCtx = SysApplication.getContext();
        return accountManager;
    }

    public String getToken() {
        return SPUtils.getString(mCtx, Constants.UserInfo.USER_TOKEN, null);
    }

    public void saveToken(String token){
        SPUtils.putString(mCtx, Constants.UserInfo.USER_TOKEN, token);
    }

    public void saveScalesId(String scalesId){
        SPUtils.putString(mCtx, Constants.UserInfo.USER_SCALES_ID, scalesId);
    }

    public String getScalesId(){
        return SPUtils.getString(mCtx, Constants.UserInfo.USER_SCALES_ID, null);
    }
}
