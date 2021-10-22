package com.shrimp.network.utils

import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder

/**
 * Created by chasing on 2021/10/21.
 */
class EncryptionInterceptor: Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        var request = chain.request()
        val oldBody = request.body()
        var body: RequestBody? = null
        if (oldBody is MultipartBody) {
            body = oldBody
        } else {
            val data = writeTo(oldBody)
            if (data != null) body = RequestBody.create(data[1] as MediaType?, data[0] as String)
        }
        // 添加统一的请求头
        val builder = request.newBuilder()
            .header("Content-Type", body?.contentType()?.toString() ?: "")
            .header("Content-Length", body?.contentLength()?.toString() ?: "0")

        //开启method(request.method(), body)方法才会重新设置请求体，因此此方法不适合用于get
        //get要求不能拥有请求体
        if ("POST" == request.method()) builder.method(request.method(), body)
        request = builder.build()
        return chain.proceed(request)
    }

    private fun writeTo(body: RequestBody?): Array<Any?>? {
        try {
            val result = arrayOfNulls<Any>(2)
            val buffer = Buffer()
            var mediaType: MediaType? = null
            if (body != null) {
                body.writeTo(buffer)
                mediaType = body.contentType()
            }
            if (mediaType == null) {
                mediaType = MediaType.parse("application/json; charset=UTF-8")
            }
            val stringBuffer = StringBuilder()
            var s: String?
            while (buffer.readUtf8Line().also { s = it } != null) {
                stringBuffer.append(s)
            }
            result[0] = stringBuffer.toString().trim { it <= ' ' } //数据，需要加密就在这里对数据进行处理
            result[1] = mediaType
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}