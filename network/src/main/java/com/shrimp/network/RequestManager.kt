package com.shrimp.network

import com.shrimp.base.utils.L
import com.shrimp.network.callback.AbstractCallBack
import com.shrimp.network.engine.ExampleEngine
import com.shrimp.network.entity.base.ResponseResult
import com.shrimp.network.entity.res.PresetWordDataInfo
import com.shrimp.network.entity.res.Tags
import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by chasing on 2021/10/21.
 */
object RequestManager {
    private lateinit var exampleEngine: ExampleEngine

    // 订阅监听
    private fun <T> observerCallBack(
        callBack: AbstractCallBack<T>,
        viewModelScope: CoroutineScope,
        call: suspend () -> ResponseResult<T>
    ): Job {
        return viewModelScope.launch(Dispatchers.Main) {
            var isCancel = false
            try {
                callBack.onStart()
                val result = withContext(Dispatchers.IO) {
                    call.invoke()
                }
                callBack.onSuccess(result)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    isCancel = true
                    callBack.onCancel()
                } else
                    callBack.onFail(e.message ?: "")
            } finally {
                if (!isCancel)
                    callBack.onComplete()
            }
        }
    }

    // region example
    fun getData(
        callBack: AbstractCallBack<PresetWordDataInfo>,
        viewModelScope: CoroutineScope
    ): Job {
        if (!::exampleEngine.isInitialized)
            exampleEngine = ExampleEngine()
        return observerCallBack(callBack, viewModelScope) {
            exampleEngine.getData()
        }
    }

    fun getTags(callBack: AbstractCallBack<List<Tags>>, viewModelScope: CoroutineScope): Job {
        if (!::exampleEngine.isInitialized)
            exampleEngine = ExampleEngine()
        return observerCallBack(callBack, viewModelScope) {
            exampleEngine.getTags()
        }
    }
    // endregion


}