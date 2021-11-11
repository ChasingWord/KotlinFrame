package com.shrimp.network.utils

import com.google.gson.TypeAdapter
import com.shrimp.network.entity.base.ResponseResult
import okhttp3.ResponseBody
import retrofit2.Converter

/**
 * Created by chasing on 2021/10/21.
 */
class JsonResponseBodyConverter<T>(private val adapter: TypeAdapter<T>) :
    Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody): T {
        val content = value.string()
        val code = content.substring(0,2)
        val json = String.format("{\"code\":\"%s\",\"data\":%s}", code, content.substring(2))
        return adapter.fromJson(json)
    }
}