package com.shrimp.network.callback

/**
 * Created by chasing on 2021/10/21.
 */
abstract class AbstractCallBack<T> : CallBack<T> {
    override suspend fun onStart() {
    }

    override suspend fun onComplete() {
    }

    override suspend fun onCancel() {

    }
}