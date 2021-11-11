package com.shrimp.network.api

import com.shrimp.network.entity.base.ResponseResult
import com.shrimp.network.entity.res.PresetWordDataInfo
import com.shrimp.network.entity.res.Tags
import kotlinx.coroutines.Deferred
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by chasing on 2021/10/21.
 */
interface ExampleApi {

    @POST("app/homepage/GetWebHomePageSearchPresetWord")
    fun getDataAsync() : Deferred<ResponseResult<PresetWordDataInfo>>

    @POST("app/course/GetCourseTagsMenuByUserId")
    fun getTags(@Header("code") code:String, @Query("currentUserId") userId:Int) : Deferred<ResponseResult<List<Tags>>>
}