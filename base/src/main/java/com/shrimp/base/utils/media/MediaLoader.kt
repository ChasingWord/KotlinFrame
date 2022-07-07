package com.shrimp.base.utils.media

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.shrimp.base.utils.FileUtil
import com.shrimp.base.utils.thread.ComparableRunnable
import com.shrimp.base.utils.thread.ThreadPoolUtil
import java.io.File

/**
 * Created by chasing on 2022/1/27.
 */
class MediaLoader(var mActivity: FragmentActivity, var mCallback: IMediaLoaderCallback) :
    LoaderManager.LoaderCallbacks<Cursor> {
    private val imageProjection = arrayOf(
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.DATE_ADDED,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.DURATION
    )

    private val selectionSingle = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
    private val selectionAll = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)")

    private val selectionArgsImage =
        arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
    private val selectionArgsVideo =
        arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
    private val selectionAllArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )

    // folder result data set
    //是否已经加载完毕 该标识为了避免 onResume时再次调用
    private var loadFinished = false

    //文件夹是否已遍历 生成过
    private var hasFolderGenerated = false

    //文件夹列表
    private var resultFolder: ArrayList<FolderBean> = ArrayList()

    private var isShowGif = true
    private var isShowVideo = false
    private var isOnlyVideo = false

    fun setShowGif(showGif: Boolean) {
        isShowGif = showGif
    }

    fun setShowVideo(showVideo: Boolean) {
        isShowVideo = showVideo
    }

    fun setOnlyVideo(onlyVideo: Boolean) {
        isOnlyVideo = onlyVideo
    }

    fun load() {
        LoaderManager.getInstance(mActivity).initLoader(0, null, this@MediaLoader)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val cursorLoader: CursorLoader
        val contentUri = MediaStore.Files.getContentUri("external")
        val selection: String
        val selectionArgs: Array<String>
        if (isOnlyVideo || !isShowVideo) {
            selection = selectionSingle
            selectionArgs = if (isOnlyVideo) {
                selectionArgsVideo
            } else {
                selectionArgsImage
            }
        } else {
            selection = selectionAll
            selectionArgs = selectionAllArgs
        }
        cursorLoader = CursorLoader(
            mActivity,
            contentUri,
            imageProjection,
            selection,
            selectionArgs, imageProjection[2] + " DESC"
        )
        loadFinished = false
        hasFolderGenerated = false
        return cursorLoader
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (!loadFinished) {
            ThreadPoolUtil.execute(object : ComparableRunnable() {
                @SuppressLint("SdCardPath")
                override fun run() {
                    loadFinished = true
                    resultFolder.clear()
                    if (data.count > 0) {
                        val allList: MutableList<MediaBean> = ArrayList()
                        val allVideoList: MutableList<MediaBean> = ArrayList()
                        data.moveToFirst()
                        var path: String
                        var name: String?
                        var mineType: String
                        var dateTime: Long
                        var duration: Long
                        var mediaBean: MediaBean
                        var folderFile: File?
                        var folder: FolderBean
                        var f: FolderBean?
                        var mediaBeanList: MutableList<MediaBean>
                        do {
                            path = data.getString(data.getColumnIndexOrThrow(imageProjection[0]))
                            name = data.getString(data.getColumnIndexOrThrow(imageProjection[1]))
                            dateTime = data.getLong(data.getColumnIndexOrThrow(imageProjection[2]))
                            mineType =
                                data.getString(data.getColumnIndexOrThrow(imageProjection[3]))
                            if (TextUtils.isEmpty(mineType) || FileUtil.getFileLength(path) == 0L) {
                                continue
                            }
                            //过滤GIF
                            if (!isShowGif && (mineType.contains(MineType.GIF) || path.endsWith(
                                    MineType.GIF
                                ))
                            ) {
                                continue
                            }

                            //创建单个实体类
                            if (mineType.contains(MineType.VIDEO)) {
                                duration =
                                    data.getLong(data.getColumnIndexOrThrow(imageProjection[4]))
                                mediaBean =
                                    MediaBean(path, name, MineType.VIDEO, dateTime, duration)
                                allVideoList.add(mediaBean)
                            } else {
                                mediaBean = MediaBean(path, name, mineType, dateTime)
                            }
                            allList.add(mediaBean)

                            //没有创建文件夹，先创建文件夹，已创建则直接加入
                            if (!hasFolderGenerated) {
                                // get all folder data
                                folderFile = File(path).parentFile
                                if (folderFile != null && folderFile.exists()) {
                                    val fp = folderFile.absolutePath
                                    f = getFolderByPath(fp)
                                    if (f == null) {
                                        folder = FolderBean(fp, mediaBean)
                                        folder.name = folderFile.name
                                        mediaBeanList = ArrayList()
                                        mediaBeanList.add(mediaBean)
                                        folder.mediaList = mediaBeanList
                                        resultFolder.add(folder)
                                    } else {
                                        f.mediaList?.add(mediaBean)
                                    }
                                }
                            }
                        } while (!data.isClosed && data.moveToNext())
                        if (!data.isClosed) { // 关闭了界面，Cursor就会被关闭，则进行判断关闭了的话就不进行后续操作了
                            //混合类型，添加所有视频集合，如果有的话
                            if (!isOnlyVideo && isShowVideo && allVideoList.isNotEmpty()) {
                                val allVideoFolder = FolderBean("/sdcard", allVideoList[0])
                                allVideoFolder.name = "所有视频"
                                allVideoFolder.mediaList = allVideoList
                                resultFolder.add(0, allVideoFolder)
                            }

                            //添加所有图片(包括视频)集合
                            if (allList.isNotEmpty()) {
                                //构造所有图片的集合
                                val allImagesFolder = FolderBean("/sdcard", allList[0])
                                when {
                                    isOnlyVideo -> allImagesFolder.name = "所有视频"
                                    isShowVideo -> allImagesFolder.name = "图片和视频"
                                    else -> allImagesFolder.name = "所有图片"
                                }
                                allImagesFolder.mediaList = allList
                                resultFolder.add(0, allImagesFolder)
                            }
                            hasFolderGenerated = true
                            mCallback.onLoadFinish(resultFolder)
                        }
                    }
                    destroy()
                }
            })
        } else {
            mCallback.onLoadFinish(resultFolder)
            destroy()
        }
    }

    private fun getFolderByPath(path: String): FolderBean? {
        for (folder in resultFolder) {
            if (TextUtils.equals(folder.path, path)) {
                return folder
            }
        }
        return null
    }

    private fun destroy() {
        ThreadPoolUtil.executeOnMainThread {
            // 销毁MediaLoader，不销毁，每次返回界面都会执行一次查询
            LoaderManager.getInstance(mActivity).destroyLoader(0)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {}


}
