package com.shrimp.network.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * Created by chasing on 2021/12/8.
 */
class LoggerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        //打印请求信息
        Log.i("LoggerInterceptor", "url:" + request.url)
        Log.i("LoggerInterceptor", "method:" + request.method)
        Log.i("LoggerInterceptor", "request-body:" + request.body)

        //记录请求耗时
        val startNs = System.nanoTime()
        //发送请求，获得相应结果 Response
        val response: Response = chain.proceed(request)
        try {
            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            //打印请求耗时
            Log.i("LoggerInterceptor", "耗时:" + tookMs + "ms")
            //使用response获得headers(),可以更新本地Cookie。
            Log.i("LoggerInterceptor", "headers==========")
            val headers = response.headers
            Log.i("LoggerInterceptor", headers.toString())
            //获得返回的body，注意此处不要使用responseBody.string()获取返回数据，原因在于这个方法会消耗返回结果的数据(buffer)
            val responseBody = response.body
            if (responseBody != null) {
                //为了不消耗buffer，我们这里使用source先获得buffer对象，然后clone()后使用
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                //获得返回的数据
                val buffer = source.buffer
                //使用前clone()下，避免直接消耗
                Log.i(
                    "LoggerInterceptor",
                    "response:" + buffer.clone().readString(StandardCharsets.UTF_8)
                )
            }
        } catch (ignored: Exception) {
        }
        return response
    }
}