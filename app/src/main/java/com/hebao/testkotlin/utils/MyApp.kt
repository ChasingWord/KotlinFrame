package com.hebao.testkotlin.utils

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.util.CoilUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shrimp.base.widgets.refresh.SmartRefreshFooter
import com.shrimp.base.widgets.refresh.SmartRefreshHeader
import okhttp3.OkHttpClient


/**
 * Created by chasing on 2021/11/3.
 */
class MyApp : Application(), ImageLoaderFactory {
    init {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> SmartRefreshHeader(context) }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> SmartRefreshFooter(context) }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .availableMemoryPercentage(0.25)
            .crossfade(true)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(this))
                    .build()
            }
            .componentRegistry {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder(this@MyApp))
                } else {
                    add(GifDecoder())
                }
                add(VideoFrameFileFetcher(this@MyApp))
                add(VideoFrameUriFetcher(this@MyApp))
                add(VideoFrameDecoder(this@MyApp))
            }
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        CrashErrorHandler.getInstance().init(this)
    }
}