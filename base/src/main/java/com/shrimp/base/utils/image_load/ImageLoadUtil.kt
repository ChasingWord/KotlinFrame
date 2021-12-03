package com.shrimp.base.utils.image_load

import android.widget.ImageView
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.onAnimationEnd
import coil.size.OriginalSize
import coil.transform.RoundedCornersTransformation
import com.shrimp.base.R
import com.shrimp.base.utils.FileUtil

/**
 * Created by chasing on 2021/12/2.
 */
object ImageLoadUtil {

    // region 无圆角
    fun load(imageView: ImageView, resId: Int) {
        imageView.load(resId)
    }

    fun load(imageView: ImageView, url: String) {
        imageView.load(url)
    }

    fun loadFile(imageView: ImageView, filePath: String) {
        imageView.load(FileUtil.getFileUri(imageView.context, filePath))
    }
    // endregion

    // region 圆角
    fun loadRound(imageView: ImageView, resId: Int, radius: Float) {
        imageView.load(resId) {
            transformations(RoundedCornersTransformation(radius).also {
                if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER)
                    size(OriginalSize)
            })
        }
    }

    fun loadRound(imageView: ImageView, url: String, radius: Float) {
        val request = ImageRequest.Builder(imageView.context)
            .data("https://www.example.com/image.jpg")
            .build()
        imageView.load(url) {
            transformations(RoundedCornersTransformation(radius).also {
                if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER)
                    size(OriginalSize)
            })
            listener(object:ImageRequest.Listener{
                override fun onStart(request: ImageRequest) {
                }

                override fun onCancel(request: ImageRequest) {
                }

                override fun onError(request: ImageRequest, throwable: Throwable) {

                }

                override fun onSuccess(request: ImageRequest, metadata: ImageResult.Metadata) {
                }
            })
        }
    }

    fun loadRoundFile(imageView: ImageView, filePath: String, radius: Float) {
        imageView.load(FileUtil.getFileUri(imageView.context, filePath)) {
            transformations(RoundedCornersTransformation(radius).also {
                if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER)
                    size(OriginalSize)
            })
        }
    }
    // endregion

}