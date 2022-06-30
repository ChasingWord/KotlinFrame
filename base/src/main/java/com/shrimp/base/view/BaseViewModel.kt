package com.shrimp.base.view

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.*

/**
 * Created by chasing on 2021/10/20.
 * 传入Application是为了可以使用getString及操作数据库等功能
 * 不使用Activity/Fragment避免内存泄露
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    LifecycleEventObserver {
    val dialogShow: MutableLiveData<Boolean> = MutableLiveData()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> onCreate()
            Lifecycle.Event.ON_START -> onStart()
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_PAUSE -> onPause()
            Lifecycle.Event.ON_STOP -> onStop()
            Lifecycle.Event.ON_DESTROY -> onDestroy()
            Lifecycle.Event.ON_ANY -> onAny()
        }
    }

    open fun onCreate() {
    }

    open fun handleIntent(intent: Intent) {}

    open fun handleBundle(bundle: Bundle) {}

    open fun loadData() {}

    open fun onStart() {
    }

    open fun onResume() {
    }

    open fun onPause() {
    }

    open fun onStop() {
    }

    open fun onDestroy() {
    }

    open fun onAny() {
    }
}