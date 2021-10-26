package com.shrimp.base.utils

import android.util.Log
import com.shrimp.base.BuildConfig
import java.lang.StringBuilder

/**
 * Created by chasing on 2021/10/22.
 */
object L {

    fun v(t: Throwable?) {
        log(Log.VERBOSE, t, null)
    }

    fun v(s1: Any?, vararg args: Any) {
        log(Log.VERBOSE, null, s1, *args)
    }

    fun v(t: Throwable?, s1: Any?, vararg args: Any) {
        log(Log.VERBOSE, t, s1, *args)
    }

    fun d(t: Throwable?) {
        log(Log.DEBUG, t, null)
    }

    fun d(s1: Any?, vararg args: Any) {
        log(Log.DEBUG, null, s1, *args)
    }

    fun d(t: Throwable?, s1: Any?, vararg args: Any) {
        log(Log.DEBUG, t, s1, *args)
    }

    fun i(t: Throwable?) {
        log(Log.INFO, t, null)
    }

    fun i(s1: Any?, vararg args: Any) {
        log(Log.INFO, null, s1, *args)
    }

    fun i(t: Throwable?, s1: Any?, vararg args: Any) {
        log(Log.INFO, t, s1, *args)
    }

    fun w(t: Throwable?) {
        log(Log.WARN, t, null)
    }

    fun w(s1: Any?, vararg args: Any) {
        log(Log.WARN, null, s1, *args)
    }

    fun w(t: Throwable?, s1: Any?, vararg args: Any) {
        log(Log.WARN, t, s1, *args)
    }

    fun e(t: Throwable?) {
        log(Log.ERROR, t, null)
    }

    fun e(s1: Any?, vararg args: Any) {
        log(Log.ERROR, null, s1, *args)
    }

    fun e(t: Throwable?, s1: Any?, vararg args: Any) {
        log(Log.ERROR, t, s1, *args)
    }

    private fun log(pType: Int, t: Throwable?, s1: Any?, vararg args: Any) {
        if (BuildConfig.DEBUG) {
//        if (true) {
            val stackTraceElement = Thread.currentThread().stackTrace[4]
            val fullClassName = stackTraceElement.className
            val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
            val lineNumber = stackTraceElement.lineNumber
            val method = stackTraceElement.methodName
            val tag = "$className:$lineNumber"
            val stringBuilder = StringBuilder()
            stringBuilder.append(method)
            stringBuilder.append("(): ")
            if (s1 != null) {
                val message = String.format(
                    (s1 as String?)!!, *args
                )
                stringBuilder.append(message)
            }
            when (pType) {
                Log.VERBOSE -> if (t != null) {
                    Log.v(tag, stringBuilder.toString(), t)
                } else {
                    Log.v(tag, stringBuilder.toString())
                }
                Log.DEBUG -> if (t != null) {
                    Log.d(tag, stringBuilder.toString(), t)
                } else {
                    Log.d(tag, stringBuilder.toString())
                }
                Log.INFO -> if (t != null) {
                    Log.i(tag, stringBuilder.toString(), t)
                } else {
                    Log.i(tag, stringBuilder.toString())
                }
                Log.WARN -> if (t != null) {
                    Log.w(tag, stringBuilder.toString(), t)
                } else {
                    Log.w(tag, stringBuilder.toString())
                }
                Log.ERROR -> if (t != null) {
                    Log.e(tag, stringBuilder.toString(), t)
                } else {
                    Log.e(tag, stringBuilder.toString())
                }
            }
        }
    }
}