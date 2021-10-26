package com.shrimp.network.engine

import com.shrimp.network.RetrofitClient
import com.shrimp.network.api.ExampleApi
import kotlinx.coroutines.Deferred

/**
 * Created by chasing on 2021/10/21.
 */
class ExampleEngine {
    var mApi: ExampleApi = RetrofitClient.getRetrofit().create(ExampleApi::class.java);

    fun getData(): Deferred<String> {
        return mApi.getData()
    }
}