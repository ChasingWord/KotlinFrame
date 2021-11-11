package com.shrimp.network.callback

import com.shrimp.network.entity.base.ResponseResult

/**
 * Created by chasing on 2021/10/21.
 */
interface CallBack<T> {
    suspend fun onStart()
    suspend fun onSuccess(responseResult: ResponseResult<T>)
    suspend fun onFail(msg: String)
    suspend fun onComplete()
    suspend fun onCancel()
}