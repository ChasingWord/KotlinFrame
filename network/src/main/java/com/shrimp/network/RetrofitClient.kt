package com.shrimp.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.shrimp.network.utils.EncryptionInterceptor
import com.shrimp.network.utils.JsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Created by chasing on 2021/10/21.
 */
object RetrofitClient {
    private const val IP = "http://192.168.100.231/"

    private lateinit var retrofit: Retrofit
    private val clock = Any()

    fun getRetrofit(): Retrofit {
        if (!::retrofit.isInitialized) {
            synchronized(clock) {
                if (!::retrofit.isInitialized)
                    retrofit = Retrofit.Builder()
                        .baseUrl(IP)
                        .client(newHttpClient())
                        .addCallAdapterFactory(CoroutineCallAdapterFactory())
                        .addConverterFactory(JsonConverterFactory.create())
                        .build()
            }
        }
        return retrofit
    }

    private fun newHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
//            .addInterceptor()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(EncryptionInterceptor())
            .build()
    }
}