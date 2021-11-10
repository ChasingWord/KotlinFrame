package com.shrimp.base.view

import android.app.Activity

/**
 * Created by chasing on 2021/11/10.
 * 因为都是在aty创建之后进行设置的监听器，所以onCreate监听不到了无需设置了
 */
interface LifeCycleListener {
    fun onStart(activity: Activity)

    fun onResume(activity: Activity)

    fun onPause(activity: Activity)

    fun onStop(activity: Activity)

    fun onDestroy(activity: Activity)
}