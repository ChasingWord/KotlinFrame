package com.shrimp.network.callback

/**
 * Created by chasing on 2021/10/21.
 * onStart()
 * 请求成功：onSuccess()->onComplete()
 * 请求失败：onFail()->onComplete()
 * 请求取消：onCancel()
 */
interface StringCallBack {
    suspend fun onStart()
    suspend fun onSuccess(data: String)
    suspend fun onFail(msg: String)
    suspend fun onCancel()
    suspend fun onComplete()
}