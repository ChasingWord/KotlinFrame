package com.shrimp.network

import com.shrimp.network.callback.AbstractStringCallBack
import com.shrimp.network.engine.ExampleEngine
import kotlinx.coroutines.*

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
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                callBack.onStart()
                callBack.onSuccess(withContext(Dispatchers.IO) {
                    call.invoke()
                })
            } catch (e: Exception) {
                callBack.onFail(e.message ?: "")
            } finally {
                callBack.onComplete()
            }
        }
    }

    // region example
    fun getData(callBack: AbstractStringCallBack, viewModelScope: CoroutineScope) {
        if (!::exampleEngine.isInitialized)
            exampleEngine = ExampleEngine()
        observerCallBack(callBack, viewModelScope){
            exampleEngine.getData()
        }
    }
    // endregion


}