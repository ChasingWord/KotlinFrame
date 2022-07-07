package com.shrimp.base.utils

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.shrimp.base.R
import com.shrimp.base.receiver.NotificationClickReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.system.exitProcess

/**
 * Created by chasing on 2021/10/22.
 */
object ActivityUtil {
    val oneClickUtil = OneClickUtil()
    private var activityStack: Stack<Activity> = Stack()

    fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**
     * 获取当前Activity并且是还未finish的
     */
    fun currentActivity(): Activity {
        var activity = activityStack.lastElement()
        if (activity!!.isFinishing && activityStack.size > 1) {
            activity = activityStack.elementAt(activityStack.size - 2)
        }
        return activity
    }

    fun removeActivity(activity: Activity?) {
        if (null != activity) activityStack.remove(activity)
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        try {
            for (i in activityStack.indices.reversed()) {
                if (!activityStack[i].isFinishing) {
                    activityStack[i].finish()
                }
            }
            activityStack.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 退出应用程序
     */
    @SuppressLint("MissingPermission")
    fun appExit(context: Context) {
        try {
            getNotificationManager(context).cancelAll() //退出应用程序取消所有状态栏消息
            finishAllActivity()
            val activityMgr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.killBackgroundProcesses(context.packageName)
            exitProcess(0)
        } catch (ignored: Exception) {
        }
    }

    //复制文本
    fun copyContentString(context: Context, content: String?) {
        val copy = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = ClipData.newPlainText(content, content)
        copy.setPrimaryClip(data)
        showToast("复制成功")
    }

    //获取粘贴板文本
    fun getCopyContentString(context: Context): String {
        val copy = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = copy.primaryClip
        if (data != null && data.itemCount > 0 && data.getItemAt(0) != null && data.getItemAt(0).text != null) return data.getItemAt(
            0
        ).text.toString()
        return ""
    }

    /**
     * 如果输入法打开，则关闭
     */
    fun closeInputMethod(context: Activity) {
        try {
            val view = context.currentFocus
            if (view != null) {
                val inputMethodManager =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (inputMethodManager.isActive) { //输入法打开状态
                    inputMethodManager.hideSoftInputFromWindow(
                        view.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * 如果输入法打开，则关闭
     * 有时context.getCurrentFocus();并不是EditText，因为点击别的UI时，焦点已经在别的UI上了
     */
    fun closeInputMethod(context: Activity, editText: EditText?) {
        if (editText == null) return
        try {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive) { //输入法打开状态
                inputMethodManager.hideSoftInputFromWindow(
                    editText.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    //如果输入法关闭，则打开
    fun openInputMethod(focusView: View, context: Activity) {
        focusView.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(focusView, InputMethodManager.SHOW_IMPLICIT)
    }

    private val requestCodeTakePhoto = 101 //拍照标识
    private val requestCodeCropPhoto = 102 //照片剪切标识
    private val requestCodeSelectVideo = 103 //选择视频
    private val requestCodeSelectFile = 104 //选择文件
    private val chooseFileTypeList = arrayOf(
        ".asp", ".aep", ".ai", ".cdr", ".doc", ".eps", ".pdf",
        ".ppt", ".psd", ".rar", ".rp", ".svg", ".xls", ".zip", ".bin"
    )

    // 打开相机
    fun takePhoto(activity: Activity): String? {
        if (activity.requestPermission(Manifest.permission.CAMERA) != 0) return null
        try {
            val filePath: String =
                FileUtil.getTempPath(activity) + File.separator + FileUtil.getPhotoFileName()
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileUtil.getFileUri(activity, filePath)
            if (uri != null) {
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                activity.startActivityForResult(takePhotoIntent, requestCodeTakePhoto)
                return filePath
            }
        } catch (e: Exception) {
            showToast(activity.resources.getString(R.string.shrimp_photo_can_not_write))
        }
        return null
    }

    //裁剪图片方法实现--任意宽高
    fun beginCrop(activity: Activity, picPath: String): String? {
        return beginCrop(activity, picPath, 0, 0, 0, 0, false, false)
    }

    //裁剪图片方法实现--widthRatio/heightRatio为裁剪的宽高比
    fun beginCrop(activity: Activity, picPath: String, widthRatio: Int, heightRatio: Int): String? {
        return beginCrop(activity, picPath, widthRatio, heightRatio, 0, 0, false, false)
    }

    //裁剪图片方法实现--裁剪头像（注：裁剪比例设置为1:1，则裁剪的显示框会变成圆形）
    fun beginCropHeadImg(activity: Activity, picPath: String): String? {
        return beginCrop(activity, picPath, 1, 1, 150, 150, false, true)
    }

    private fun beginCrop(
        activity: Activity, picPath: String, widthRatio: Int, heightRatio: Int,
        outputX: Int, outputY: Int, copyToCache: Boolean, circleCrop: Boolean,
    ): String? {
        var picPathTemp = picPath
        try {
            if (copyToCache) picPathTemp = FileUtil.getOperationalFilePath(activity, picPathTemp)
            val uri: Uri? = FileUtil.getFileUri(activity, picPathTemp)
            if (uri != null) {
                val intent = Intent("com.android.camera.action.CROP")
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uri, "image/*")
                intent.putExtra("crop", "true") // crop=true 有这句才能出来最后的裁剪页面.
                if (widthRatio > 0 && heightRatio > 0) {
                    intent.putExtra("aspectX", widthRatio) // 这两项为裁剪框的比例.
                    intent.putExtra("aspectY", heightRatio) // x:y=1:1
                }
                if (outputX > 0 && outputY > 0) {
                    // 是裁剪图片宽高，注意如果return-data=true情况下,其实得到的是缩略图，并不是真实拍摄的图片大小，
                    // 而原因是拍照的图片太大，所以这个宽高当你设置很大的时候发现并不起作用，就是因为返回的原图是缩略图，但是作为头像还是够清晰了
                    intent.putExtra("outputX", outputX) //图片输出大小
                    intent.putExtra("outputY", outputY)
                }
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                intent.putExtra("circleCrop", circleCrop) //设置裁剪框是否圆形，部分手机支持
                val uriTempFile: Uri? = if (FileUtil.isBelow29()) {
                    // 部分低于29的手机不能直接保存到公共目录，会报权限错误，所以需要区分保存
                    Uri.fromFile(File(FileUtil.getCachePath(activity) + FileUtil.getPhotoFileName()))
                } else {
                    //android11图片裁剪保存的时候不能访问应用私有目录，只能保存在共有目录
                    FileUtil.getDownloadPicUri(activity, FileUtil.getPhotoFileName())
                }
                if (uriTempFile != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTempFile)
                    activity.startActivityForResult(intent, requestCodeCropPhoto)
                    return FileUtil.getFilePathByUri(activity, uriTempFile)
                } else {
                    showToast("图片创建异常！")
                    return null
                }
            }
        } catch (e: java.lang.Exception) {
            if (!copyToCache && e.message != null && e.message!!.contains("not have permission")) return beginCrop(
                activity,
                picPathTemp,
                widthRatio,
                heightRatio,
                outputX,
                outputY,
                true,
                true) else showToast("图片生成失败！" + e.message)
        }
        return null
    }

    //打开系统视频选择界面
    fun openVideoSelectActivity(activity: Activity) {
        if (activity.checkAndRequestReadStoragePermission() != 0) return
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        activity.startActivityForResult(
            Intent.createChooser(intent, "选择要导入的视频"),
            requestCodeSelectVideo
        )
    }

    //打开系统文件选择界面
    fun openFileSelectActivity(activity: Activity) {
        if (activity.checkAndRequestReadStoragePermission() != 0) return
        val intent = Intent()
        val sb = StringBuilder()
        val mimeTypeList = arrayOfNulls<String>(chooseFileTypeList.size)
        for (i in chooseFileTypeList.indices) {
            mimeTypeList[i] = FileTypeUtil.getMIMEType(chooseFileTypeList[i])
            sb.append(mimeTypeList[i]).append("|")
        }
        if (sb.length > 0) sb.deleteCharAt(sb.length - 1)
        intent.type = sb.toString()
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypeList)
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        activity.startActivityForResult(
            Intent.createChooser(intent, "选择要上传的文件"),
            requestCodeSelectFile
        )
    }

    //打开系统设置界面
    fun openSettingUI(activity: Activity) {
        activity.startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    //打开权限设置界面
    fun openPermissionSettingUI(activity: Activity) {
        try {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:" + activity.packageName)
            activity.startActivity(intent)
        } catch (ignored: java.lang.Exception) {
        }
    }

    //拨打电话
    fun callPhone(context: Context, telPhone: String) {
        try {
            if ((context as Activity).requestPermission(Manifest.permission.CALL_PHONE) != 0) return
            val telUri = Uri.parse("tel:$telPhone")
            val telIntent = Intent(Intent.ACTION_CALL, telUri)
            context.startActivity(telIntent)
        } catch (e: Exception) {
            showToast("拨打电话失败！")
        }
    }

    /**
     * Android8.0对通知进行了管理，需要注册通知渠道，将会显示在系统应用信息界面上
     *
     * @param id   通知id，发送通知时使用对应id进行发送
     * @param name 通知渠道名称，将在系统应用信息界面上显示包含的通知
     * 例如："聊天通知"
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun registerNotification(context: Context, id: String?, name: CharSequence?) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel1 = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
        channel1.enableLights(true) //设置通知出现时的闪灯（如果 android 设备支持的话）
        channel1.lightColor = Color.GREEN //闪灯颜色
        channel1.enableVibration(true) // 设置通知出现时的震动（如果 android 设备支持的话）
        // channel1.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知，默认为true
        notificationManager.createNotificationChannel(channel1)
    }

    /**
     * 发送通知
     *
     * @param context        上下文对象
     * @param id             Android8.0以上需要的通知id，对应的是某个id 的通知渠道
     * @param title          标题
     * @param content        内容文本
     * @param intent         点击意图
     * @param notificationId 通知id，对应的是某类通知，id 相同的通知将会在通知栏进行覆盖
     */
    fun sendNotification(
        context: Context,
        id: String,
        title: String,
        content: String,
        intent: Intent,
        notificationId: Int,
    ) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP) //会清除跳转目标activity及之上的所有activity
        val clickIntent = Intent(context, NotificationClickReceiver::class.java)
        clickIntent.putExtra("intent", intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotificationAbove26(
                context, id, title, content, clickIntent, notificationId
            )
        else
            sendNotificationBelow26(context, id, title, content, clickIntent, notificationId)
    }

    private var PRE_SHOW_NOTIFICATION_TIME: Long = 0

    @SuppressLint("LaunchActivityFromNotification", "UnspecifiedImmutableFlag")
    private fun sendNotificationBelow26(
        context: Context,
        id: String,
        title: String,
        content: String,
        clickIntent: Intent,
        notificationId: Int,
    ) {
        val curTime = System.currentTimeMillis()
        val needSound = curTime - PRE_SHOW_NOTIFICATION_TIME > 1000
        PRE_SHOW_NOTIFICATION_TIME = curTime
        val builder = NotificationCompat.Builder(context, id)
        builder.setAutoCancel(true) //点击后让通知将消失
            .setContentTitle(title)
            .setContentText(content)
            .setTicker("$title-$content") //.setNumber(sendId)
            .setWhen(System.currentTimeMillis()) //通知产生的时间，会在通知信息里显示
            .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级，高优先级才会显示横幅（即刚接收到信息时的悬浮弹窗）
            .setOngoing(false) //true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            //向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：  requires VIBRATE permission
            .setDefaults(if (needSound) Notification.DEFAULT_ALL else Notification.DEFAULT_LIGHTS)
            .setSmallIcon(R.drawable.shrimp_channel_icon)
        //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.nlargelogo));
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
            notificationId,
            builder.build()
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @TargetApi(Build.VERSION_CODES.O)
    private fun sendNotificationAbove26(
        context: Context,
        id: String,
        title: String,
        content: String,
        clickIntent: Intent,
        notificationId: Int,
    ) {
//        long curTime = System.currentTimeMillis();
//        boolean needSound = curTime - PRE_SHOW_NOTIFICATION_TIME > 1000;
//        PRE_SHOW_NOTIFICATION_TIME = curTime;
        val builder = Notification.Builder(context, id)
        //icon title text必须包含，不然影响桌面图标小红点的展示
        builder.setAutoCancel(true) //点击后让通知将消失
            .setContentTitle(title)
            .setContentText(content)
            .setTicker("$title-$content")
            .setWhen(System.currentTimeMillis()) //通知产生的时间，会在通知信息里显示
            .setOngoing(false) //true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            //                .setNumber(2) //长按桌面图标时显示的通知的数量
            //向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：  requires VIBRATE permission
            //                .setDefaults(needSound ? Notification.DEFAULT_ALL : Notification.DEFAULT_LIGHTS)
            .setSmallIcon(R.drawable.shrimp_channel_icon)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
            notificationId,
            builder.build()
        )
    }
}

/**
 * 请求权限，返回是否需要请求
 * WRITE_EXTERNAL_STORAGE权限包含了READ_EXTERNAL_STORAGE，所以只进行WRITE_EXTERNAL_STORAGE权限请求即可
 * 0 不需要请求
 * 1 需要请求且发起了请求
 * -1 需求请求，但因为是永久拒绝所以没有发起请求
 */
@SuppressLint("CheckResult")
fun Activity.requestPermission(
    vararg permission: String,
    jumpPermissionSettingUI: Boolean = false,
): Int =
    // 6.0以下不需要请求权限，AndroidManifest有写即可
    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> 0
        permission.isNotEmpty() -> {
            var needRequest = false
            val needRequestPermission = ArrayList<String>()
            for (permissionItem in permission) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permissionItem
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    needRequest = true


                    val shouldShow =
                        ActivityCompat.shouldShowRequestPermissionRationale(this, permissionItem)
                    if (!shouldShow) {
                        val hadRequest =
                            ObjectCacheUtil.readObjectBySP(this, permissionItem, false) as Boolean
                        if (hadRequest) {
                            // 请求过被永久拒绝返回false
                            if (jumpPermissionSettingUI)
                                ActivityUtil.openPermissionSettingUI(this)
                        } else {
                            // 没有申请过返回false
                            ObjectCacheUtil.saveObjectBySP(this, permissionItem, true)
                            needRequestPermission.add(permissionItem)
                        }
                    } else {
                        // 请求过被普通拒绝返回true
                        needRequestPermission.add(permissionItem)
                        ObjectCacheUtil.saveObjectBySP(this, permissionItem, true)
                    }
                }
            }

            if (needRequest) {
                if (needRequestPermission.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            requestPermissions(
                                needRequestPermission.toTypedArray(),
                                0
                            )
                        } catch (e: Exception) {
                        }
                    }
                    1
                } else -1
            } else 0
        }
        else -> 0
    }

// 进行检测是否拥有权限
fun Activity.checkPermission(permission: String) =
    ActivityCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED

// 检测并请求读存储权限
fun Activity.checkAndRequestReadStoragePermission(jumpPermissionSettingUI: Boolean = false): Int =
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
        jumpPermissionSettingUI = jumpPermissionSettingUI)

// 检测并请求写存储权限
fun Activity.checkAndRequestWriteStoragePermission(jumpPermissionSettingUI: Boolean = false): Int =
    if (!FileUtil.isBelow29()) 0 else requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        jumpPermissionSettingUI = jumpPermissionSettingUI)


