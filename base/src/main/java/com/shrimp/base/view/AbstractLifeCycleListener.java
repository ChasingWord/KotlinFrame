package com.shrimp.base.view;

import android.app.Activity;

/**
 * Created by chasing on 2019/7/9.
 */
public abstract class AbstractLifeCycleListener implements LifeCycleListener {
    /**
     * 如果是在Fragment进行添加，则设置为false
     * 用于在移除Fragment的时候同时移除其监听
     */
    public boolean isAddForActivity = true;

    public void onStart(Activity activity) {
    }

    public void onResume(Activity activity) {
    }

    public void onPause(Activity activity) {
    }

    public void onStop(Activity activity) {
    }

    public void onDestroy(Activity activity) {
    }
}
