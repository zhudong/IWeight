package com.axecom.iweight.conf;

import com.axecom.iweight.base.SysApplication;
import com.axecom.iweight.manager.MacManager;
import com.axecom.iweight.utils.LogUtils;

/**
 * Created by Administrator on 2018-5-3.
 */

public class Constants {
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;
    public static final String URL = "http://nong.yingzy.com/api/";
    public static final String MAC_TEST = "84:73:03:5b:ba:bb";

    public static final String ADMIN_CARD_NUMBER = "A29529D6";
    public static final String ADMIN_CARD_PWD = "885911";

    public class UserInfo{
        public static final String USER_TOKEN = "user_token";
        public static final String USER_SCALES_ID = "scales_id";

    }

}
