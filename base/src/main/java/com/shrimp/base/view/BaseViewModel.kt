package com.shrimp.base.view

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 * Created by chasing on 2021/10/20.
 * 传入Application是为了可以使用getString及操作数据库等功能
 * 不使用Activity/Fragment避免内存泄露
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application), ILifeCycle {
    val dialogShow: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate() {}
    open fun handleIntent(intent: Intent) {}
    open fun loadingData() {}
    override fun onStart() {}
    override fun onResume() {}
    override fun onPause() {}
    override fun onStop() {}
    override fun onDestroy() {}
}