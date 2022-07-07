package com.shrimp.base.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.core.content.FileProvider
import com.shrimp.base.utils.thread.ComparableRunnable
import com.shrimp.base.utils.thread.ThreadPoolUtil
import java.io.*
import java.util.*

/**
 * Created by chasing on 2021/10/22.
 */
object FileUtil {
    //默认压缩精度
    private const val NORMAL_QUALITY = 80

    // 检测是否存在SD卡
    fun hasSDCard(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()
    }

    private fun getFileProvider(context: Context): String {
        return context.packageName + ".fileprovider"
    }

    fun isBelow29(): Boolean {
        // 使用Environment.isExternalStorageLegacy()来检查APP的运行模式
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                Environment.isExternalStorageLegacy()
    }

    fun isFilePath(path: String): Boolean {
        return if (TextUtils.isEmpty(path)) false else path.startsWith("file://") || path.startsWith(
            "/storage/emulated/0"
        )
    }

    fun exists(context: Context, path: String): Boolean {
        return when {
            TextUtils.isEmpty(path) -> false
            isBelow29() -> {
                val file = File(path)
                file.exists()
            }
            else -> {
                var afd: AssetFileDescriptor? = null
                val cr = context.contentResolver
                try {
                    val uri: Uri = getFileUri(context, path) ?: return false
                    afd = cr.openAssetFileDescriptor(uri, "r")
                    if (afd == null) return false
                } catch (e: FileNotFoundException) {
                    return false
                } finally {
                    if (afd != null) {
                        try {
                            afd.close()
                        } catch (ignored: Exception) {
                        }
                    }
                }
                true
            }
        }
    }

    // 判断私有目录是否存在，不存在则进行创建
    private fun createSelfDirIfNotExist(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    fun deleteSelfFile(filePath: String) {
        if (TextUtils.isEmpty(filePath)) return
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            file.delete()
        }
    }

    // 递归删除目录下的所有文件及子目录下所有文件
    fun deleteSelfDir(dirPath: String) {
        if (TextUtils.isEmpty(dirPath)) return
        deleteSelfDir(File(dirPath))
    }

    private fun deleteSelfDir(dir: File) {
        try {
            if (null == dir || !dir.exists() || !dir.isDirectory || dir.listFiles() == null) {
                return
            }
            for (file in Objects.requireNonNull(dir.listFiles())) {
                if (file.isFile) file.delete() // 删除所有文件
                else if (file.isDirectory) deleteSelfDir(file) // 递规的方式删除文件夹
            }
            //此时目录为空，可以删除
            dir.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 每次启动应用删除overTime时间前未发送成功的聊天图片及拍照图片
    private fun deleteOverTimeSelfFile(file: File, overTime: Long) {
        if (file == null) return
        try {
            if (file.exists() && file.isDirectory && file.listFiles() != null) {
                for (subFile in Objects.requireNonNull(file.listFiles())) {
                    if (subFile.isFile) {
                        val lastModifyTime = subFile.lastModified()
                        if (System.currentTimeMillis() - lastModifyTime >= overTime) subFile.delete()
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    // 获取app的私有目录路径
    private fun getAppFilePath(context: Context, directory: String): String {
        val filePath: String = if (hasSDCard()) {
            val externalFilesDir = context.getExternalFilesDir(directory)
            if (externalFilesDir != null) externalFilesDir.absolutePath else context.filesDir.path
        } else {
            context.filesDir.path
        }
        return filePath
    }

    fun getDownloadPath(): String {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + File.separator
        val file = File(path)
        if (!file.exists()) file.mkdirs()
        return path
    }

    // 获取缓存路径
    fun getCachePath(context: Context): String {
        val cachePath: String = if (hasSDCard() && context.externalCacheDir != null) {
            context.externalCacheDir!!.path
        } else {
            context.cacheDir.path
        }
        return cachePath
    }

    // name需要后缀
    fun getDownloadPicUri(context: Context, imageName: String): Uri? {
        val mimeType: String = FileTypeUtil.getMIMEType(imageName)
        if (!mimeType.startsWith("image")) {
            return if (mimeType.startsWith("video")) getDownloadVideoUri(
                context,
                imageName
            ) else getDownloadDocumentUri(context, imageName)
        }

        //设置保存参数到ContentValues中
        val contentValues = ContentValues()
        //设置文件名
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        } else {
            //Android Q以下版本
            val fileDir =
                File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES)
            if (!fileDir.exists()) {
                fileDir.mkdir()
            }
            contentValues.put(MediaStore.Images.Media.DATA, fileDir.absolutePath + "/" + imageName)
        }
        //设置文件类型
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        //获取不到正确的文件类型
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*")

        //执行insert操作，向系统文件夹中添加文件
        //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    @SuppressLint("DefaultLocale")
    fun getDownloadDocumentUri(context: Context, fileName: String): Uri? {
        var fileName = fileName
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //设置保存参数到ContentValues中
            val contentValues = ContentValues()
            //设置文件名
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            //RELATIVE_PATH是相对路径不是绝对路径
            //Download是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            contentValues.put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis())
            //设置文件类型
            contentValues.put(MediaStore.Downloads.MIME_TYPE, FileTypeUtil.getMIMEType(fileName))
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            var insert = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )
            if (insert == null) {
                context.contentResolver.delete(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    MediaStore.Downloads.DISPLAY_NAME + "='" + fileName + "'", null
                )
                insert = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                while (insert == null) {
                    if (fileName.contains("(") && fileName.contains(")") && fileName.lastIndexOf(")") > fileName.lastIndexOf(
                            "("
                        )
                    ) {
                        val number = fileName.substring(
                            fileName.lastIndexOf("(") + 1,
                            fileName.lastIndexOf(")")
                        )
                        fileName = try {
                            var integer = number.toInt()
                            integer++
                            String.format(
                                "%s%s%s",
                                fileName.substring(0, fileName.lastIndexOf("(") + 1),
                                integer,
                                fileName.substring(fileName.lastIndexOf(")"))
                            )
                        } catch (e: NumberFormatException) {
                            String.format(
                                "%s%s%s",
                                fileName.substring(0, fileName.lastIndexOf(".")),
                                "(1)",
                                fileName.substring(fileName.lastIndexOf("."))
                            )
                        }
                    } else {
                        var temp: String
                        var i = 1
                        while (i > 0) {
                            temp = String.format(
                                "%s%s%s",
                                fileName.substring(0, fileName.lastIndexOf(".")),
                                String.format("(%d)", i),
                                fileName.substring(fileName.lastIndexOf("."))
                            )
                            if (!exists(context, temp)) {
                                fileName = temp
                                break
                            }
                            i++
                        }
                    }
                    contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    insert = context.contentResolver.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                }
            }
            insert
        } else {
            getFileUri(context, getDownloadPath() + fileName)
        }
    }

    fun getDownloadVideoUri(context: Context, videoName: String): Uri? {
        val mimeType: String = FileTypeUtil.getMIMEType(videoName)
        if (!mimeType.startsWith("video")) {
            return if (mimeType.startsWith("image")) getDownloadPicUri(
                context,
                videoName
            ) else getDownloadDocumentUri(context, videoName)
        }

        //设置保存参数到ContentValues中
        val contentValues = ContentValues()
        //设置文件名
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, videoName)
        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
        } else {
            //Android Q以下版本
            val fileDir =
                File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_MOVIES)
            if (!fileDir.exists()) {
                fileDir.mkdir()
            }
            contentValues.put(MediaStore.Video.Media.DATA, fileDir.absolutePath + "/" + videoName)
        }
        //设置文件类型
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, FileTypeUtil.getMIMEType(videoName))
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/*")
        //执行insert操作，向系统文件夹中添加文件
        //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
        return context.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    @TargetApi(Build.VERSION_CODES.Q)
    fun deleteDownloadDocument(context: Context, fileName: String) {
        context.contentResolver.delete(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            MediaStore.Downloads.DISPLAY_NAME + "='" + fileName + "'", null
        )
    }

    // 获取临时缓存目录
    fun getTempPath(context: Context): String {
        val tempPath: String = getAppFilePath(context, "temp")
        createSelfDirIfNotExist(tempPath)
        return tempPath
    }

    fun isTempFile(filePath: String): Boolean {
        return filePath.contains(File.separator + "temp" + File.separator)
    }

    // 获取log存储路径
    fun getLogPath(context: Context): String {
        val logPath: String = getAppFilePath(context, "log")
        createSelfDirIfNotExist(logPath)
        return logPath
    }

    // 获取聊天图片存储路径  注：每三天清理一次
    fun getChatPicPath(context: Context): String {
        //存放聊天发送用的拍照路径和压缩图片路径，在请求成功之后才会删除图片
        val chatCamera: String = getAppFilePath(context, "chat_camera")
        createSelfDirIfNotExist(chatCamera)
        return chatCamera
    }

    // 视频第一帧图片的存放位置  注：每七天清理一次
    fun getVideoThumbnailPath(context: Context): String {
        val tempPath: String = getAppFilePath(context, "thumbnail")
        createSelfDirIfNotExist(tempPath)
        return tempPath
    }

    //每次启动应用删除3天前未发送成功的聊天图片及拍照图片
    //每次启动应用删除7天前的视频第一帧的缓存
    fun deleteOverTimeChatPic(context: Context) {
        deleteOverTimeSelfFile(
            File(getChatPicPath(context)),
            (3 * 24 * 60 * 60 * 1000).toLong()
        )
        deleteOverTimeSelfFile(
            File(getVideoThumbnailPath(context)),
            (7 * 24 * 60 * 60 * 1000).toLong()
        )
        deleteSelfDir(File(getCachePath(context)))
    }

    //删除临时文件
    fun deleteTempFile(context: Context) {
        deleteSelfDir(getTempPath(context))
    }

    // 文件保存删除后，调用媒体库数据更新（图片才能显示到相册里面或从相册里面删除），filePath是文件路径全名，包括后缀哦
    fun updateGallery(context: Context, filePath: String) {
        MediaScannerConnection.scanFile(
            context, arrayOf(filePath), null
        ) { _: String?, _: Uri? -> }
    }

    // --                    以上为文件夹操作                      --//
    // 判断文件路径对应的文件是否存在，如果存在则通过加"(1)"的形式进行重新创建一个文件不存在的路径进行返回
    @SuppressLint("DefaultLocale")
    fun judgeFilePath(context: Context, filePath: String): String {
        var filePath = filePath
        if (exists(context, filePath)) {
            if (filePath.contains("(") && filePath.contains(")") && filePath.lastIndexOf(")") > filePath.lastIndexOf(
                    "("
                )
            ) {
                val number =
                    filePath.substring(filePath.lastIndexOf("(") + 1, filePath.lastIndexOf(")"))
                try {
                    var integer = number.toInt()
                    integer++
                    while (integer > 0) {
                        filePath = String.format(
                            "%s%s%s",
                            filePath.substring(0, filePath.lastIndexOf("(") + 1),
                            integer,
                            filePath.substring(filePath.lastIndexOf(")"))
                        )
                        if (!exists(context, filePath)) break
                        integer++
                    }
                } catch (e: NumberFormatException) {
                    filePath = String.format(
                        "%s%s%s",
                        filePath.substring(0, filePath.lastIndexOf(".")),
                        "(1)",
                        filePath.substring(filePath.lastIndexOf("."))
                    )
                }
            } else {
                var temp: String
                var i = 1
                while (i > 0) {
                    temp = String.format(
                        "%s%s%s",
                        filePath.substring(0, filePath.lastIndexOf(".")),
                        String.format("(%d)", i),
                        filePath.substring(filePath.lastIndexOf("."))
                    )
                    if (!exists(context, temp)) {
                        filePath = temp
                        break
                    }
                    i++
                }
            }
        }
        return filePath
    }

    /**
     * 产生除了视频、音频、网页文件外，打开其他类型文件的Intent
     *
     * @param filePath 文件路径
     */
    fun openFile(context: Context, filePath: String) {
        if (!exists(context, filePath)) {
            //如果文件不存在
            showToast("打开失败，原因：文件已经被移动或者删除")
        } else {
            try {
                val fileUri = getFileUri(context, filePath)
                if (fileUri != null) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.setDataAndType(fileUri, FileTypeUtil.getMIMEType(filePath))
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                showToast("未找到可以打开该文件的软件")
            }
        }
    }

    // 根据路径得到该本地文件的长度(Byte)
    fun getFileLength(path: String): Long {
        if (TextUtils.isEmpty(path)) return 0
        val f = File(path)
        return f.length()
    }

    // 根据路径得到该本地文件的长度(MB)
    fun getFileLengthForMB(path: String): Float {
        val length = getFileLength(path)
        var shortLength = length / 1024f
        shortLength /= 1024f
        return shortLength
    }

    // 将单位为B的length转换成KB/MB/GB，并添加单位一起返回
    @SuppressLint("DefaultLocale")
    fun getShortFileLength(length: Long): String {
        return if (length >= 1024) {
            var shortLength = length / 1024f
            if (shortLength >= 1024) {
                shortLength /= 1024f
                if (shortLength >= 1024) {
                    shortLength /= 1024f
                    String.format("%.2fGB", shortLength)
                } else String.format("%.2fMB", shortLength)
            } else String.format("%.2fKB", shortLength)
        } else length.toString() + "B"
    }

    // 将传递过来的bitmap图片进行压缩保存
    // isPrivatePath:存储的路径是否是私有路径，私有目录可以直接用File操作
    // isPrivatePath为false存放的是公共目录，则picPath直接传文件名称即可
    fun saveTempBitmap(
        context: Context,
        bm: Bitmap,
        picPath: String,
        isPrivatePath: Boolean
    ): String? {
        if (isBelow29() || isPrivatePath) {
            try {
                if (picPath.lastIndexOf('/') > 0) {
                    val root = File(picPath.substring(0, picPath.lastIndexOf('/')))
                    if (!root.exists()) root.mkdirs()
                }
                val f = File(picPath)
                if (f.exists()) f.delete()
                val out = FileOutputStream(f)
                bm.compress(Bitmap.CompressFormat.JPEG, NORMAL_QUALITY, out)
                out.flush()
                out.close()
                return picPath
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            try {
                val downloadPicUri = getDownloadPicUri(context, picPath)
                if (downloadPicUri != null) {
                    val out = context.contentResolver.openOutputStream(downloadPicUri)
                    if (out != null) {
                        bm.compress(Bitmap.CompressFormat.JPEG, NORMAL_QUALITY, out)
                        out.flush()
                        out.close()
                        return getFilePathByUri(context, downloadPicUri)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    //得到输入流字节大小(可用于计算网络文件字节大小)
    fun getInputStreamByte(inputStream: InputStream): ByteArray? {
        val b = ByteArray(1024)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var len: Int
        try {
            while (inputStream.read(b).also { len = it } != -1) {
                byteArrayOutputStream.write(b, 0, len)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                byteArrayOutputStream.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return byteArrayOutputStream.toByteArray()
    }

    // 获取文件的字节数据
    fun getFileByteArray(context: Context, filePath: String): ByteArray? {
        val inputStream = getInputStream(context, filePath)
        return if (inputStream != null) getInputStreamByte(inputStream) else null
    }

    /**
     * 按偏移量及需要读取的大小获取字节数据
     *
     * @param filePath  文件路径
     * @param offset    已经读取的偏移量
     * @param blockSize 需要读取的块大小
     * @return 该文件块的字节数据
     */
    fun getFileByBlockSize(
        context: Context,
        filePath: String,
        offset: Long,
        blockSize: Int
    ): ByteArray? {
        val inputStream = getInputStream(context, filePath)
        val fileLength = getFileLength(filePath)
        if (inputStream != null) {
            try {
                var curBlockSize: Int //当前需要读取的块大小
                val result: ByteArray
                when {
                    fileLength == offset -> { //因为offset已经是文件大小了，所以返回null代表读取完毕了
                        return null
                    }
                    fileLength >= offset + blockSize -> {
                        curBlockSize = blockSize
                        result = ByteArray(blockSize)
                    }
                    else -> {
                        curBlockSize = (fileLength - offset).toInt()
                        result = ByteArray(curBlockSize)
                    }
                }
                var readByte: ByteArray
                readByte = if (curBlockSize >= 2048) ByteArray(2048) else ByteArray(curBlockSize)
                if (offset > 0) inputStream.skip(offset)
                var readSize: Int
                var count = 0
                while (inputStream.read(readByte)
                        .also { readSize = it } != -1 && curBlockSize - readSize >= 0
                ) {
                    System.arraycopy(readByte, 0, result, count * 2048, readSize)
                    count++
                    curBlockSize -= readSize
                    if (curBlockSize < 2048) readByte = ByteArray(curBlockSize)
                    if (curBlockSize == 0) break
                }
                return result
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    //得到文件的后缀名并将其转换为小写(不包含'.')
    fun getFileExtension(url: String): String {
        var url = url
        if (!TextUtils.isEmpty(url)) {
            url = url.replace("\\", "/")
            val fragment = url.lastIndexOf('#')
            if (fragment > 0) {
                url = url.substring(0, fragment)
            }
            val query = url.lastIndexOf('?')
            if (query > 0) {
                url = url.substring(0, query)
            }
            val filenamePos = url.lastIndexOf('/')
            val filename = if (0 <= filenamePos) url.substring(filenamePos + 1) else url
            if (filename.isNotEmpty()) {
                val dotPos = filename.lastIndexOf('.')
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1).lowercase(Locale.getDefault())
                }
            }
        }
        return ""
    }

    // 生存图片文件名
    fun getPhotoFileName(): String {
        return NumberUtil.generateGUID() + ".jpg"
    }

    // 获取视频时长
    fun getDuration(context: Context, path: String): Long {
        var mmr: MediaMetadataRetriever? = null
        try {
            val fileUri = getFileUri(context, path)
            if (fileUri != null) {
                mmr = MediaMetadataRetriever()
                mmr.setDataSource(context, fileUri)
                return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
            }
        } catch (ignored: Exception) {
        } finally {
            mmr?.release()
        }
        return 0
    }

    // 获取视频宽高
    fun getVideoWidthHeight(context: Context, path: String): IntArray {
        val widthHeight = IntArray(2)
        var mmr: MediaMetadataRetriever? = null
        try {
            mmr = MediaMetadataRetriever()
            mmr.setDataSource(context, getFileUri(context, path))
            widthHeight[0] =
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()
            widthHeight[1] = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
                .toInt()
        } catch (ignored: Exception) {
        } finally {
            mmr?.release()
        }
        return widthHeight
    }

    private fun isLocalStorageDocument(cxt: Context, uri: Uri): Boolean {
        return getFileProvider(cxt) == uri.authority
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        val column = "_data"
        val projection = arrayOf(column)
        try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                .use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndexOrThrow(column))
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    //由Uri读取路径
    fun getFilePathByUri(context: Context, uri: Uri?): String? {
        if (uri == null) return null
        else if (isLocalStorageDocument(context, uri)) {
            // The path is the id
            return DocumentsContract.getDocumentId(uri)
        } else if (DocumentsContract.isDocumentUri(context, uri)) { // DocumentProvider
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if ("home".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:".toRegex(), "")
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) { // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return if (contentUri == null) null
                else getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) { // MediaStore (and general)
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) { // File
            return uri.path
        }
        return null
    }

    fun getInputStream(context: Context, filePath: String): InputStream? {
        return if (isBelow29()) {
            try {
                FileInputStream(filePath)
            } catch (e: FileNotFoundException) {
                null
            }
        } else { //android10以上
            val uri = getFileUri(context, filePath)
            if (uri != null) try {
                return context.contentResolver.openInputStream(uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            null
        }
    }

    fun getOutputStream(context: Context, filePath: String): OutputStream? {
        return if (isBelow29()) {
            try {
                FileOutputStream(filePath)
            } catch (e: FileNotFoundException) {
                null
            }
        } else { //android10以上
            val uri = getFileUri(context, filePath)
            if (uri != null) try {
                return context.contentResolver.openOutputStream(uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            null
        }
    }

    private fun getFileUriBelow29(context: Context, path: String): Uri? {
        val file = File(path)
        return if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                context.applicationContext,
                getFileProvider(context),
                file
            )
        } else {
            Uri.fromFile(file)
        }
    }

    //获取公共文件对应的Uri
    fun getFileUri(context: Context?, path: String): Uri? {
        if (context == null || TextUtils.isEmpty(path)) {
            throw NullPointerException()
        }
        //Android10以下需要有读权限才能进行操作ContentResolver.query
        if (context is Activity && context.checkAndRequestReadStoragePermission() != 0) return null
        var uri: Uri? = null
        var cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ",
            arrayOf(path),
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        }
        if (uri == null) {
            cursor = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.Media._ID),
                MediaStore.Video.Media.DATA + "=? ",
                arrayOf(path),
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            }
        }
        if (uri == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var fileName = path
            if (path.contains("/")) fileName = path.substring(path.lastIndexOf("/") + 1)
            if (path.contains("\\")) fileName = path.substring(path.lastIndexOf("\\") + 1)
            cursor = context.contentResolver.query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Downloads._ID),
                MediaStore.Downloads.DISPLAY_NAME + "=? ",
                arrayOf(fileName),
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                uri = Uri.withAppendedPath(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
            }
        }
        try {
            cursor?.close()
        } catch (ignored: Exception) {
        }
        if (uri == null) uri = getFileUriBelow29(context, path)
        return uri
    }

    //获取公共文件对应的Uri -- 运行在子线程，采用监听器回调结果
    fun getFileUri(
        context: Context?,
        path: String,
        listener: OnFileUriBackListener
    ) {
        if (context == null || TextUtils.isEmpty(path)) {
            throw NullPointerException()
        }
        //Android10以下需要有读权限才能进行操作ContentResolver.query
        if (context is Activity && context.checkAndRequestReadStoragePermission() != 0) {
            listener.onFileUriBack(null)
        } else {
            ThreadPoolUtil.execute(object : ComparableRunnable() {
                override fun run() {
                    var uri: Uri? = null
                    var cursor = context.contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Images.Media._ID),
                        MediaStore.Images.Media.DATA + "=? ",
                        arrayOf(path),
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        val id =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                        uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    }
                    if (uri == null) {
                        cursor = context.contentResolver.query(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            arrayOf(MediaStore.Video.Media._ID),
                            MediaStore.Video.Media.DATA + "=? ",
                            arrayOf(path),
                            null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
                            val id =
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                            uri = Uri.withAppendedPath(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        }
                    }
                    if (uri == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        var fileName = path
                        if (path.contains("/")) fileName = path.substring(path.lastIndexOf("/") + 1)
                        if (path.contains("\\")) fileName =
                            path.substring(path.lastIndexOf("\\") + 1)
                        cursor = context.contentResolver.query(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            arrayOf(MediaStore.Downloads._ID),
                            MediaStore.Downloads.DISPLAY_NAME + "=? ",
                            arrayOf(fileName),
                            null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
                            val id =
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                            uri =
                                Uri.withAppendedPath(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
                        }
                    }
                    try {
                        cursor?.close()
                    } catch (ignored: Exception) {
                    }
                    if (uri == null) uri = getFileUriBelow29(context, path)
                    val finalUri = uri
                    ThreadPoolUtil.executeOnMainThread { listener.onFileUriBack(finalUri) }
                }
            })
        }
    }

    interface OnFileUriBackListener {
        fun onFileUriBack(uri: Uri?)
    }

    //获取对应的可操作路径
    //通过路径进行判断是否是私有目录，私有目录则可以直接使用，公共目录则需要复制文件到私有目录，之后才能对路径文件操作
    fun getOperationalFilePath(context: Context, path: String): String {
        val uri: Uri = getFileUri(context, path) ?: return ""
        if (isLocalStorageDocument(context, uri)) {
            return path
        } else {
            val `is` = getInputStream(context, path)
            if (`is` != null) {
                var fos: FileOutputStream? = null
                val savePath: String =
                    getCachePath(context) + File.separator + "temp_" + NumberUtil.generateGUID() + path.substring(
                        path.lastIndexOf(".")
                    )
                try {
                    fos = FileOutputStream(savePath)
                    if (!isBelow29()) { //android10以上
                        android.os.FileUtils.copy(`is`, fos)
                    } else {
                        val bytes = ByteArray(2048)
                        while (`is`.read(bytes) != -1) {
                            fos.write(bytes)
                        }
                        fos.flush()
                    }
                } catch (ignored: IOException) {
                } finally {
                    try {
                        `is`.close()
                        fos?.close()
                    } catch (ignored: IOException) {
                    }
                }
                return savePath
            }
        }
        return path
    }

    // 保存视图View到系统相册  注意使用子线程执行
    fun saveViewToPicFile(context: Context, v: View): String? {
        val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        v.draw(c)
        val picUri =
            getDownloadPicUri(context, System.currentTimeMillis().toString() + ".png")
        var out: OutputStream? = null
        var filePathByUri: String?
        try {
            filePathByUri = getFilePathByUri(context, picUri)
            if (filePathByUri != null) {
                out = getOutputStream(context, filePathByUri)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                updateGallery(context, filePathByUri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            filePathByUri = null
        } finally {
            try {
                if (out != null) {
                    out.flush()
                    out.close()
                }
            } catch (ignored: Exception) {
            }
        }
        return filePathByUri
    }
}