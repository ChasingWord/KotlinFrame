package com.shrimp.base.view;

import android.app.Activity;

/**
 * Created by chasing on 2019/7/9.
 * 因为都是在aty创建之后进行设置的监听器，所以onCreate监听不到了无需设置了
 */
public interface LifeCycleListener {
    void onStart(Activity activity);

    void onResume(Activity activity);

    void onPause(Activity activity);

    void onStop(Activity activity);

    void onDestroy(Activity activity);
}
