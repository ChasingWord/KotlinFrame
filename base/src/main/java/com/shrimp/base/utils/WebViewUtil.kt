package com.shrimp.base.utils

import android.annotation.SuppressLint
import android.util.Base64
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@SuppressLint("SetJavaScriptEnabled")
fun WebView.initSetting() {
    requestFocus() //如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件
    //        mContentWv.setInitialScale(1); //设置页面加载比例，1代表直接将整个网页加载到界面上，会自动缩放进行适应到屏幕尺寸
    val webSettings = settings
    //        webSettings.setTextZoom(100);//设置WebView中加载页面字体变焦百分比，默认100。//嵌入一个H5页面，使H5页面的字体不会随用户自己调整的系统字体变化而变化
    webSettings.javaScriptEnabled = true
    webSettings.domStorageEnabled = true
    webSettings.defaultTextEncodingName = "UTF-8"
    webSettings.javaScriptCanOpenWindowsAutomatically = true
    webSettings.blockNetworkImage = false //解决图片不显示
    webSettings.loadsImagesAutomatically = true
    // 根据 HTTP 协议头里的 Cache-Control（或 Expires）和 Last-Modified（或 Etag）等字段来控制文件缓存的机制，
    // 现在设置的缓存方式是根据网页设置的这几个值判断的，没有过期的话就会读取本地缓存，过期了则进行重新加载
    // 本地缓存在内存不足的时候也会被清理，在网络请求失败的时候显示错误页面
    webSettings.cacheMode = WebSettings.LOAD_DEFAULT
    webSettings.useWideViewPort = true // 这个很关键，设置webview推荐使用的窗口
    webSettings.loadWithOverviewMode = true // 设置webview加载的页面的模式，也设置为true
    webSettings.displayZoomControls = false // 隐藏webview缩放按钮
    webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL //解决字符串换行问题
    webSettings.mediaPlaybackRequiresUserGesture = false
    webSettings.builtInZoomControls = false //关闭缩放功能support zoom
    webSettings.setSupportZoom(false)
    // 设置浏览器标识ua
//        val ua = webSettings.userAgentString
//        webSettings.setUserAgentString(ua + "; HXBApp/" + AppUtils.getVersionName(context))

    //允许混合加载，即http与https可以同时加载，避免加载的是https的url，而里面的图片视频使用的是http的情况
    webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
}

//注意：文件的生成格式不同，需要用不同的格式去读取
fun WebView.loadLocalProtocolUTF_8(rawResId: Int) {
    try {
        val `is` = context.resources.openRawResource(rawResId)
        val isr = InputStreamReader(`is`, StandardCharsets.UTF_8)
        val br = BufferedReader(isr)
        val sb = StringBuilder()
        var str: String?
        while (br.readLine().also { str = it } != null) {
            sb.append(str)
        }
        val content = Base64.encodeToString(sb.toString().toByteArray(), Base64.NO_PADDING)
        loadData(content, "text/html; charset=UTF-8", "base64")
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

//注意：文件的生成格式不同，需要用不同的格式去读取
fun WebView.loadLocalProtocol(rawResId: Int) {
    try {
        val `is` = context.resources.openRawResource(rawResId)
        val isr = InputStreamReader(`is`, "GBK")
        val br = BufferedReader(isr)
        val sb = StringBuilder()
        var str: String?
        while (br.readLine().also { str = it } != null) {
            sb.append(str)
        }
        val content = Base64.encodeToString(sb.toString().toByteArray(), Base64.NO_PADDING)
        loadData(content, "text/html; charset=UTF-8", "base64")
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun WebView.removeAndDestroy() {
    if (parent is ViewGroup) (parent as ViewGroup).removeView(this)
    stopLoading()
    // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
    settings.javaScriptEnabled = false
    clearHistory()
    removeAllViews()
    destroy()
}