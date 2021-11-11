package com.shrimp.network.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Created by chasing on 2021/11/11.
 */
object JsonUtil {

    val gson = Gson()

    inline fun <reified T> fromJson(jsonString: String): T {
        return gson.fromJson(jsonString, T::class.java)
    }

    fun <T> fromJsonList(jsonString: String, token: TypeToken<List<T>>): List<T> {
        return gson.fromJson(jsonString, token.type)
    }

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }
}