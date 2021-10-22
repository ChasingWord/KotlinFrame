package com.shrimp.network.callback

/**
 * Created by chasing on 2021/10/21.
 */
interface StringCallBack {
    fun onStart()
    fun onSuccess(data: String)
    fun onFail(msg: String)
    fun onComplete()
}