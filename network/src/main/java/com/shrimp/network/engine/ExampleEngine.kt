package com.shrimp.network.engine

import com.shrimp.network.RetrofitClient
import com.shrimp.network.api.ExampleApi
import kotlinx.coroutines.cancel

/**
 * Created by chasing on 2021/10/21.
 */
class ExampleEngine {
    private var mApi: ExampleApi = RetrofitClient.getRetrofit().create(ExampleApi::class.java)

    suspend fun getData(): String {
        return mApi.getDataAsync().await()
    }
}