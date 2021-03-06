package com.axecom.iweight.manager;

import android.app.Activity;


import com.axecom.iweight.utils.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Longer on 2016/11/1.
 */
public class ActivityController {

    public static List<Activity> activities = new ArrayList<Activity>();
    static boolean mIsFinishAll = false;

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        if (!mIsFinishAll) {
            activities.remove(activity);
        }
    }

    public static void finishAll() {
        LogUtils.d("finishAll");
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 安全结束Activity的方法
     * @param whenTheArrayListFinish 借口回调,防止未完成遍历的情况就删除或者增加集合操作
     */
    public static void finishAllSafe(WhenTheArrayListFinish whenTheArrayListFinish) {
        mIsFinishAll = true;
        LogUtils.d("finishAll");
        Iterator<Activity> iterator = activities.iterator();
        while (iterator.hasNext()) {
            Activity next = iterator.next();
            if (!next.isFinishing()) {
                next.finish();
                iterator.remove();
            }
        }
        whenTheArrayListFinish.readComplete();
        mIsFinishAll = false;
    }

    public interface WhenTheArrayListFinish {
        void readComplete();
    }

    public static void finishActivityOutOfMainActivity(){
        for (Activity activity : activities) {
            String c = activity.getComponentName().getClassName();
            if (!activity.isFinishing() && !activity.getComponentName().getClassName().equals("com.fuexpress.kr.MainActivity")) {
                activity.finish();
            }
        }
    }

    public static List<Activity> cardActiviies = new ArrayList<Activity>();

    public static void cardAddActivity(Activity activity){
        cardActiviies.add(activity);
    }

    public static void cardFinish(){
        for (Activity activity : cardActiviies) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
