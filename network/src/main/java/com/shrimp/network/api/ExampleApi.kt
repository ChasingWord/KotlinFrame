package com.shrimp.network.api

import kotlinx.coroutines.Deferred
import retrofit2.http.POST

/**
 * Created by chasing on 2021/10/21.
 */
interface ExampleApi {

    @POST("app/homepage/GetWebHomePageSearchPresetWord")
    fun getData() : Deferred<String>
}