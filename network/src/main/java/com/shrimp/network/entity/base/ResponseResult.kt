package com.shrimp.network.entity.base

/**
 * Created by chasing on 2021/10/21.
 */
data class ResponseResult<T>(
    var code: Int = 0,
    var data: T? = null,
    var message: String = ""
)