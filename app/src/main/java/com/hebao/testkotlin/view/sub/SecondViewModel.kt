package com.hebao.testkotlin.view.sub

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shrimp.base.utils.ActivityUtil
import com.shrimp.base.view.BaseViewModel
import com.shrimp.network.RequestManager
import com.shrimp.network.callback.AbstractStringCallBack

/**
 * Created by chasing on 2021/10/25.
 */
class SecondViewModel(application: Application) : BaseViewModel(application) {
    val title: MutableLiveData<String> = MutableLiveData("第二个Aty")
    val text: MutableLiveData<String> = MutableLiveData("")
    val data: MutableLiveData<ArrayList<String>> = MutableLiveData(ArrayList())

    override fun loadingData() {
        RequestManager.getData(object: AbstractStringCallBack() {
            override fun onSuccess(data: String) {
                text.value = data
            }

            override fun onFail(msg: String) {
                ActivityUtil.showToast(getApplication(), msg)
            }
        }, viewModelScope)

        for (i in 0..50){
            if (i % 2 == 0)
                data.value?.add("0")
            else
                data.value?.add("3")
        }
    }
}