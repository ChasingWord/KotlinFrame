package com.shrimp.base.view

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 * Created by chasing on 2021/10/20.
 * 传入Application是为了可以使用getString及操作数据库等功能
 * 不使用Activity/Fragment避免内存泄露
 */
abstract class BaseFragmentViewModel(application: Application) : AndroidViewModel(application),
    IFragmentLifeCycle {
    val dialogShow: MutableLiveData<Boolean> = MutableLiveData()

    override fun onAttach() {}
    override fun onCreate() {}
    override fun onCreateView() {}
    override fun onActivityCreated() {}
    open fun handleBundle(bundle: Bundle?) {}
    open fun loadingData() {}
    override fun onStart() {}
    override fun onResume() {}
    override fun onPause() {}
    override fun onStop() {}
    override fun onDestroyView() {}
    override fun onDestroy() {}
    override fun onDetach() {}
}