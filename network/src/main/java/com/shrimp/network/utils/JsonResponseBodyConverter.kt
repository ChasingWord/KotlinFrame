package com.shrimp.network.utils

import okhttp3.ResponseBody
import retrofit2.Converter

/**
 * Created by chasing on 2021/10/21.
 */
class JsonResponseBodyConverter: Converter<ResponseBody, String> {
    override fun convert(value: ResponseBody): String {
        return value.string()
    }
}