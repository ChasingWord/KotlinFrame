package com.shrimp.base.utils.image_load

import android.widget.ImageView
import coil.dispose
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import coil.size.Size
import coil.transform.RoundedCornersTransformation
import com.shrimp.base.R
import com.shrimp.base.utils.FileUtil
import java.io.File

/**
 * Created by chasing on 2021/12/2.
 * 注：切圆角时，如果ImageView为FitCenter样式则需要设置size(Size.ORIGINAL)
 * 不设置Size.ORIGINAL，在切圆角时会按照控件大小裁剪图片，导致不是FitCenter样式
 *
 * Coil可以直接使用视频URL进行加载第一帧
 */
object ImageLoadUtil {

    const val ERROR_TYPE_NORMAL = 0;
    const val ERROR_TYPE_LONG = 1;

    private fun getErrorResId(errorType: Int) = when (errorType) {
        ERROR_TYPE_LONG -> R.drawable.default_pic_long
        else -> R.drawable.default_pic
    }

    // region 无圆角
    fun load(imageView: ImageView, resId: Int) {
        imageView.dispose()
        imageView.load(resId)
    }

    fun load(imageView: ImageView, url: String) {
        imageView.dispose()
        imageView.load(url) {
            error(getErrorResId(ERROR_TYPE_NORMAL))
        }
    }

    fun loadFile(imageView: ImageView, filePath: String) {
        imageView.dispose()
        imageView.load(FileUtil.getFileUri(imageView.context, filePath)) {
            error(getErrorResId(ERROR_TYPE_NORMAL))
        }
    }

    fun loadVideoFile(imageView: ImageView, filePath: String, frameMillis: Long = 1000) {
        imageView.dispose()
        imageView.load(File(filePath)) {
            videoFrameMillis(frameMillis)
        }
    }
    // endregion

    // region 圆角
    /*
        圆角方法都需要添加该判断：
        transformations(RoundedCornersTransformation(radius).also {
            // 不设置Size.ORIGINAL，在切圆角时会按照控件大小裁剪图片，导致不是FitCenter样式
            if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER)
                size(Size.ORIGINAL)
        })
        错误图片的圆角设置（直接使用error不会有圆角效果）：
        listener(object : ImageRequest.Listener {
            override fun onError(request: ImageRequest, throwable: Throwable) {
                loadRound(imageView, getErrorResId(ERROR_TYPE_NORMAL), radius)
            }
        })
     */

    fun loadRound(imageView: ImageView, resId: Int, radius: Float) {
        imageView.dispose()
        imageView.load(resId) {
            transformations(RoundedCornersTransformation(radius).also {
                // 不设置Size.ORIGINAL，在切圆角时会按照控件大小裁剪图片，导致不是FitCenter样式
                if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER)
                    size(Size.ORIGINAL)
            })
        }
    }

    fun loadRound(imageView: ImageView, url: String, radius: Float) {
        imageView.dispose()
        imageView.load(url) {
            transformations(RoundedCornersTransformation(radius).also {
                if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER)
                    size(Size.ORIGINAL)
            })
            listener(object : ImageRequest.Listener {
                override fun onError(request: ImageRequest, result: ErrorResult) {
                    loadRound(imageView, getErrorResId(ERROR_TYPE_NORMAL), radius)
                }
            })
        }
    }

    fun loadRound(imageView: ImageView, url: String, radius: Float, errorType: Int) {
        imageView.dispose()
        imageView.load(url) {
            transformations(RoundedCornersTransformation(radius).also {
                if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER)
                    size(Size.ORIGINAL)
            })
            listener(object : ImageRequest.Listener {
                override fun onError(request: ImageRequest, result: ErrorResult) {
                    loadRound(imageView, getErrorResId(errorType), radius)
                }
            })
        }
    }

    fun loadRound(
        imageView: ImageView, url: String, radius: Float, errorType: Int,
        strokeWidth: Float, strokeColor: Int,
    ) {
        imageView.dispose()
        imageView.load(url) {
            transformations(
                CoilRoundedCornersTransformation(
                    radius,
                    strokeWidth,
                    strokeColor
                ).also {
                    if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER)
                        size(Size.ORIGINAL)
                })
            listener(object : ImageRequest.Listener {
                override fun onError(request: ImageRequest, result: ErrorResult) {
                    loadRound(imageView, getErrorResId(errorType), radius)
                }
            })
        }
    }

    fun loadRoundFile(imageView: ImageView, filePath: String, radius: Float) {
        imageView.dispose()
        imageView.load(FileUtil.getFileUri(imageView.context, filePath)) {
            transformations(RoundedCornersTransformation(radius).also {
                if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER)
                    size(Size.ORIGINAL)
            })
            listener(object : ImageRequest.Listener {
                override fun onError(request: ImageRequest, result: ErrorResult) {
                    loadRound(imageView, getErrorResId(ERROR_TYPE_NORMAL), radius)
                }
            })
        }
    }
    // endregion
}