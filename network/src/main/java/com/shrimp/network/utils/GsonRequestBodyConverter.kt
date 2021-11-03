package com.shrimp.network.utils

import androidx.annotation.NonNull
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.StandardCharsets

/**
 * Created by chasing on 2021/10/21.
 */
class GsonRequestBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>): Converter<T, RequestBody> {
    private val mediaType = MediaType.parse("application/json; charset=UTF-8")
    private val charset = StandardCharsets.UTF_8

    @Throws(IOException::class)
    override fun convert(@NonNull value: T): RequestBody? {
        val buffer = Buffer()
        val writer: Writer = OutputStreamWriter(buffer.outputStream(), charset)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return RequestBody.create(mediaType, buffer.readByteString())
    }
}