package com.shrimp.base.view

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*

/**
 * Created by chasing on 2021/10/20.
 * 传入Application是为了可以使用getString及操作数据库等功能
 * 不使用Activity/Fragment避免内存泄露
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    LifecycleObserver {
    val dialogShow: MutableLiveData<Boolean> = MutableLiveData()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate() {
    }

    open fun handleIntent(intent: Intent) {}

    open fun loadingData() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
    }
}