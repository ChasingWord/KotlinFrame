package com.hebao.testkotlin.utils

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shrimp.base.utils.CrashErrorHandler
import com.shrimp.base.widgets.refresh.SmartRefreshFooter
import com.shrimp.base.widgets.refresh.SmartRefreshHeader

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
            .memoryCache { MemoryCache.Builder(this).maxSizePercent(0.25).build() }
            .crossfade(true)
            .diskCache {
                DiskCache.Builder()
                    .directory(applicationContext.cacheDir.resolve("image_cache"))
                    .build()
            }
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(VideoFrameDecoder.Factory())
                add(SvgDecoder.Factory())
            }
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        CrashErrorHandler.init(this)
    }
}