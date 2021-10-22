package com.shrimp.network.callback

import com.shrimp.network.bean.base.ResponseResult

/**
 * Created by chasing on 2021/10/21.
 */
interface CallBack<T> {
    fun onStart()
    fun onSuccess(responseResult: ResponseResult<T>)
    fun onFail(msg: String)
    fun onComplete()
}