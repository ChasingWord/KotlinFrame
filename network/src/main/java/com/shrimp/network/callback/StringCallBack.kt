package com.shrimp.network.callback

/**
 * Created by chasing on 2021/10/21.
 */
interface StringCallBack {
    suspend fun onStart()
    suspend fun onSuccess(data: String)
    suspend fun onFail(msg: String)
    suspend fun onComplete()
}