package com.shrimp.base.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo

/**
 * Created by chasing on 2021/10/22.
 */
object NetUtil {
    /**
     * 判断网络是否连接
     */
    fun isConnected(context: Context): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (null != info && info.isConnected) {
            return info.state == NetworkInfo.State.CONNECTED
        }
        return false
    }

    /**
     * 判断是否是wifi连接
     */
    fun isWifi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo!!.type == ConnectivityManager.TYPE_WIFI
    }
}