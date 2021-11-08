package com.shrimp.base.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*

/**
 * Created by chasing on 2021/10/22.
 */
object AppUtil {
    private const val CODE_INSTALL_PACKAGES = 150

    private lateinit var saveFilePath: String
    private var isForceUpdate = false

    @SuppressLint("PackageManagerGetSignatures")
    private fun getPackageInfo(ctx: Context): PackageInfo? {
        var info: PackageInfo? = null
        try {
            info = ctx.packageManager.getPackageInfo(ctx.packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return info
    }

    /**
     * 获取版本号 4.9
     *
     * @return 当前应用的版本名称
     */
    fun getVersionName(context: Context): String? {
        return try {
            getPackageInfo(context)!!.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 获取软件版本号(对应AndroidManifest.xml下android:VersionCode)  50
     *
     * @return 当前应用的版本id
     */
    fun getVersionCode(context: Context): Int {
        var versionCode = 1
        try {
            versionCode = getPackageInfo(context)!!.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionCode
    }

    //获取进程名称
    fun getProcessName(cxt: Context): String? {
        val pid = Process.myPid()
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }

    /**
     * 安装apk文件
     * android11在请求完“安装未知应用”权限之后会清了应用数据
     * 所以需要将安装信息进行保存，在请求完权限的时候才能进行读取安装
     */
    fun installApk(_saveFilePath: String, _isForceUpdate: Boolean) {
        var saveFilePath = _saveFilePath
        var isForceUpdate = _isForceUpdate
        val activity: Activity = ActivityUtil.currentActivity()
        if (TextUtils.isEmpty(saveFilePath)) {
            if (!TextUtils.isEmpty(this@AppUtil.saveFilePath)) {
                saveFilePath = this@AppUtil.saveFilePath
                isForceUpdate = this@AppUtil.isForceUpdate
            }
        } else {
            this@AppUtil.saveFilePath = saveFilePath
            this@AppUtil.isForceUpdate = isForceUpdate
        }
        if (!TextUtils.isEmpty(saveFilePath)) {
            // 通过Intent安装APK文件
            val i = Intent(Intent.ACTION_VIEW)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.setDataAndType(
                FileUtil.getFileUri(activity, saveFilePath),
                "application/vnd.android.package-archive"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            activity.startActivity(i)
            if (isForceUpdate) // 延迟退出应用，避免导致安装进程请求应用验证fileprovider权限失败
                Handler(Looper.getMainLooper()).postDelayed({
                    ActivityUtil.appExit(activity)
                }, 1000)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity(activity: Activity) {
        val packageURI = Uri.parse("package:" + activity.packageName)
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        activity.startActivityForResult(intent, CODE_INSTALL_PACKAGES)
    }

}