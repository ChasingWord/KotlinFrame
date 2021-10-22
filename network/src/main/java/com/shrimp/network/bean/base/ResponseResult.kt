package com.shrimp.network.bean.base

/**
 * Created by chasing on 2021/10/21.
 */
class ResponseResult<T> {
    var code: Int = 0
    var data: T? = null
    var message: String = ""
}