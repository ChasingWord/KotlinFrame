package com.shrimp.network

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

    // region engine对象创建
    private lateinit var exampleEngineImp: ExampleEngine
    private val exampleLock = Any() //每个engine创建需要单独的锁，避免影响其它engine获取
    private val exampleEngine: ExampleEngine
        get() {
            if (!::exampleEngineImp.isInitialized) {
                synchronized(exampleLock) {
                    if (!::exampleEngineImp.isInitialized) {
                        exampleEngineImp = ExampleEngine()
                    }
                }
            }
            return exampleEngineImp
        }
    // endregion

    // region 订阅监听
    private fun <T> observerCallBack(
        callBack: AbstractCallBack<T>,
        viewModelScope: CoroutineScope,
        call: suspend () -> ResponseResult<T>
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
    // endregion

    // region example
    fun getData(
        callBack: AbstractCallBack<PresetWordDataInfo>,
        viewModelScope: CoroutineScope
    ): Job {
        return observerCallBack(callBack, viewModelScope) {
            exampleEngine.getData()
        }
    }

    fun getTags(callBack: AbstractCallBack<List<Tags>>, viewModelScope: CoroutineScope): Job {
        return observerCallBack(callBack, viewModelScope) {
            exampleEngine.getTags()
        }
    }
    // endregion
}