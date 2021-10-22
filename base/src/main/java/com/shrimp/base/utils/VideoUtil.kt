package com.shrimp.base.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.shrimp.base.view.AbstractLifeCycleListener
import com.shrimp.base.view.BaseActivity
import com.shrimp.base.view.BaseFragmentActivity
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by chasing on 2021/10/22.
 */
object VideoUtil {
    private val mTaskList = HashMap<String, Job>()

    /**
     * 获取视频的第一帧图片
     * 如果是用于列表的情况，需要注意：
     * 在请求回调的时候，判断ImageView加载的是否是原来的drawable，进行判断在请求第一帧的过程中加载了别的图片，
     * 则请求第一帧回调之后就不用设置到ImageView上了
     */
    fun getImageForVideo(
        context: Context, videoPath: String, listener: OnLoadVideoImageListener?,
        coroutineScope: CoroutineScope
    ) = runBlocking {
        val f = getOutputMediaFile(context, videoPath)
        if (f.exists()) {
            if (listener != null) {
                if (context is Activity && context.isFinishing) return@runBlocking
                listener.onLoadImage(context, f)
            }
        } else {
            val lifeCycleListener: AbstractLifeCycleListener =
                object : AbstractLifeCycleListener() {
                    override fun onDestroy(activity: Activity) {
                        val atyName = activity.toString()
                        val removeList = ArrayList<String>()
                        for (mutableEntry in mTaskList) {
                            if (mutableEntry.key.contains(atyName)) {
                                mutableEntry.value.cancel(null)
                                removeList.add(mutableEntry.key)
                            }
                        }

                        for (key in removeList) {
                            mTaskList.remove(key)
                        }
                    }
                }
            if (context is BaseActivity) {
                context.addLifeCycleListener(lifeCycleListener)
            } else if (context is BaseFragmentActivity) {
                context.addLifeCycleListener(lifeCycleListener)
            }

            val launch = coroutineScope.launch(Dispatchers.IO) {
                var mmr: MediaMetadataRetriever? = null
                var out: FileOutputStream? = null
                var file: File? = getOutputMediaFile(context, videoPath)
                try {
                    //保存图片
                    if (file == null || !f.exists()) {
                        if (videoPath.startsWith("http")) { //获取网络视频第一帧图片
                            mmr = MediaMetadataRetriever()
                            mmr.setDataSource(videoPath, HashMap())
                            val bitmap = mmr.frameAtTime
                            out = FileOutputStream(f)
                            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
                            out.flush()
                            out.close()
                            bitmap.recycle()
                        } else  //本地视频，Glide可以直接加载本地视频，会显示第一帧
                            file = File(videoPath)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    file = null
                } finally {
                    mmr?.release()
                    out?.close()
                }

                if (context is Activity && context.isFinishing) return@launch
                listener?.onLoadImage(context, file)
                mTaskList.remove(context.toString() + videoPath)
                if (context is BaseActivity)
                    context.removeLifeCycleListener(lifeCycleListener)
                else if(context is BaseFragmentActivity)
                    context.removeLifeCycleListener(lifeCycleListener)
            }
            mTaskList[context.toString() + videoPath] = launch
            launch.join()
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private fun getOutputMediaFile(context: Context, path: String): File {
        // Create a media file name
        var path = path
        path = path.replace("\\", "/")
        val fileName = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'))
        return File(
            FileUtil.getVideoThumbnailPath(context) + File.separator + fileName + ".jpg_"
        )
    }

    interface OnLoadVideoImageListener {
        fun onLoadImage(context: Context?, file: File?)
    }
}