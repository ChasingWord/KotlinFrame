package com.shrimp.base.utils

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.text.DateFormat
import java.util.*

/**
 * Created by chasing on 2022/3/1.
 */
object CrashErrorHandler : Thread.UncaughtExceptionHandler {
    private var mContext: Application? = null

    //用来存储设备信息和异常信息
    private val info: MutableMap<String, String> = HashMap()

    /**
     * 初始化
     */
    fun init(application: Application?) {
        mContext = application
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        //捕获信息
        collectDeviceInfo()
        collectErrorMsg(ex)
    }

    /**
     * 收集设备参数信息
     */
    private fun collectDeviceInfo() {
        try {
            val pm = mContext!!.packageManager // 获得包管理器
            val pi = pm.getPackageInfo(mContext!!.packageName,
                PackageManager.GET_ACTIVITIES) // 得到该应用的信息，即主Activity
            if (pi != null) {
                val versionName = if (pi.versionName == null) "null" else pi.versionName
                val versionCode = pi.versionCode.toString() + ""
                info["versionName"] = versionName
                info["versionCode"] = versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val fields = Build::class.java.declaredFields // 反射机制
        var obj: Any?
        try {
            for (field in fields) {
                obj = field[""]
                if (obj == null) continue
                field.isAccessible = true
                info[field.name] = obj.toString()
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 收集并打印错误信息到Logcat
     */
    private fun collectErrorMsg(ex: Throwable): String {
        val sb = StringBuilder()
        for ((key, value) in info) {
            sb.append(key).append("=").append(value).append("\r\n")
        }
        val date = Date()
        val dateTimeInstance = DateFormat.getDateTimeInstance()
        val format = dateTimeInstance.format(date)
        sb.append(format).append("\r\n").append("Android报错信息:\r\n")
        val writer: Writer = StringWriter()
        val pw = PrintWriter(writer)
        ex.printStackTrace(pw)
        pw.close() // 记得关闭
        val result = writer.toString()
        //将奔溃日志打印到控制台，开发人员调试用
        sb.append(result)
        Log.e("exception", "uncaughtException errorReport=$result")
        return sb.toString()
    }
}