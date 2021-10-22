package com.shrimp.network.utils

import androidx.annotation.NonNull
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by chasing on 2021/10/21.
 */
class JsonConverterFactory private constructor(private val gson: Gson) : Converter.Factory() {
    companion object{
        fun create(): JsonConverterFactory {
            return JsonConverterFactory(Gson())
        }
    }

    // type对应的是RequestBody的类型
    override fun requestBodyConverter(
        @NonNull type: Type?,
        @NonNull parameterAnnotations: Array<Annotation?>?,
        @NonNull methodAnnotations: Array<Annotation?>?,
        @NonNull retrofit: Retrofit?
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonRequestBodyConverter(gson, adapter)
    }

    // type对应的是Flowable<T>里面的T类型
    override fun responseBodyConverter(
        @NonNull type: Type?,
        @NonNull annotations: Array<Annotation?>?,
        @NonNull retrofit: Retrofit?
    ): Converter<ResponseBody, *> {
        return JsonResponseBodyConverter()
    }
}