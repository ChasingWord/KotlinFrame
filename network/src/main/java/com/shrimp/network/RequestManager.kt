package com.shrimp.network

import com.shrimp.network.callback.AbstractStringCallBack
import com.shrimp.network.engine.ExampleEngine
import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by chasing on 2021/10/21.
 */
object RequestManager {
    private lateinit var exampleEngine: ExampleEngine

    // 订阅监听
    private fun observerCallBack(
        callBack: AbstractStringCallBack,
        viewModelScope: CoroutineScope,
        call: suspend () -> String
    ): Job {
        return viewModelScope.launch(Dispatchers.Main) {
            var isCancel = false
            try {
                callBack.onStart()
                callBack.onSuccess(withContext(Dispatchers.IO) {
                    call.invoke()
                })
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
    fun getData(callBack: AbstractStringCallBack, viewModelScope: CoroutineScope): Job {
        if (!::exampleEngine.isInitialized)
            exampleEngine = ExampleEngine()
        return observerCallBack(callBack, viewModelScope) {
            exampleEngine.getData()
        }
    }
    // endregion


}