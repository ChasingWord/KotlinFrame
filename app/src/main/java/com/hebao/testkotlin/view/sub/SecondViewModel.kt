package com.hebao.testkotlin.view.sub

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shrimp.base.utils.ActivityUtil
import com.shrimp.base.view.BaseViewModel
import com.shrimp.network.callback.AbstractStringCallBack

/**
 * Created by chasing on 2021/10/25.
 */
class SecondViewModel(application: Application) : BaseViewModel(application) {
    val title: MutableLiveData<String> = MutableLiveData("第二个Aty")
    val text: MutableLiveData<String> = MutableLiveData("")
    val data: MutableLiveData<ArrayList<String>> = MutableLiveData(ArrayList())

    private var repository = SecondRepository()

    override fun loadingData() {
        repository.getData(getApplication(), object : AbstractStringCallBack() {
            override suspend fun onStart() {
                dialogShow.value = true
            }

            override suspend fun onSuccess(data: String) {
                text.value = data
            }

            override suspend fun onFail(msg: String) {
                ActivityUtil.showToast(getApplication(), msg)
            }

            override suspend fun onComplete() {
                dialogShow.value = false
            }
        }, viewModelScope)

        refresh(50)
    }

    fun refresh(num: Int) {
        val list = ArrayList<String>()
        for (i in 0..num) {
            if (i % 2 == 0)
                list.add("0")
            else
                list.add("3")
        }
        data.value = list
    }
}