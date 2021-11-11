package com.shrimp.network.engine

import com.shrimp.network.RetrofitClient
import com.shrimp.network.api.ExampleApi
import com.shrimp.network.entity.base.ResponseResult
import com.shrimp.network.entity.res.PresetWordDataInfo
import com.shrimp.network.entity.res.Tags
import kotlinx.coroutines.cancel

/**
 * Created by chasing on 2021/10/21.
 */
class ExampleEngine {
    private var mApi: ExampleApi = RetrofitClient.getRetrofit().create(ExampleApi::class.java)

    suspend fun getData(): ResponseResult<PresetWordDataInfo> = mApi.getDataAsync().await()

    suspend fun getTags(): ResponseResult<List<Tags>> = mApi.getTags("744677", 311).await()
}