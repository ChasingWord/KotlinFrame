package com.shrimp.base.view

import android.app.Activity

/**
 * Created by chasing on 2021/11/10.
 */
abstract class AbstractLifeCycleListener : LifeCycleListener {
    /**
     * 如果是在Fragment进行添加，则设置为false
     * 用于在移除Fragment的时候同时移除其监听
     */
    var isAddForActivity = true

    override fun onStart(activity: Activity) {}

    override fun onResume(activity: Activity) {}

    override fun onPause(activity: Activity) {}

    override fun onStop(activity: Activity) {}

    override fun onDestroy(activity: Activity) {}
}