package com.hebao.testkotlin.view.sub

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shrimp.base.utils.ActivityUtil
import com.shrimp.base.view.BaseViewModel
import com.shrimp.network.callback.AbstractCallBack
import com.shrimp.network.entity.base.ResponseResult
import com.shrimp.network.entity.res.PresetWordDataInfo
import com.shrimp.network.entity.res.Tags

/**
 * Created by chasing on 2021/10/25.
 */
class SecondViewModel(application: Application) : BaseViewModel(application) {
    val title: MutableLiveData<String> = MutableLiveData("第二个Aty")
    val text: MutableLiveData<String> = MutableLiveData("")
    val data: MutableLiveData<List<Tags>> = MutableLiveData(ArrayList())

    private var repository = SecondRepository()

    override fun loadingData() {
        repository.getLocalData(getApplication(), viewModelScope)
        get()
        repository.getTags(object : AbstractCallBack<List<Tags>>() {
            override suspend fun onSuccess(responseResult: ResponseResult<List<Tags>>) {
                this@SecondViewModel.data.value = responseResult.data
            }

            override suspend fun onFail(msg: String) {
                ActivityUtil.showToast(getApplication(), msg)
            }
        }, viewModelScope)
    }

    private fun get() {
        repository.getData(object : AbstractCallBack<PresetWordDataInfo>() {
            override suspend fun onStart() {
                dialogShow.value = true
            }

            override suspend fun onSuccess(responseResult: ResponseResult<PresetWordDataInfo>) {
                text.value = responseResult.data?.Name
            }

            override suspend fun onFail(msg: String) {
                ActivityUtil.showToast(getApplication(), msg)
            }

            override suspend fun onCancel() {
            }

            override suspend fun onComplete() {
                dialogShow.value = false
            }
        }, viewModelScope)
    }
}