package com.shrimp.base.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.NonNull

/**
 * Created by chasing on 2021/10/22.
 */
class GenericTools {
    /**
     * 返回屏幕高度（不包含虚拟导航栏）
     */
    fun getScreenHeight(activity: Activity?): Int {
        return activity?.resources?.displayMetrics?.heightPixels ?: 0
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//            WindowMetrics metrics = activity.getWindowManager().getCurrentWindowMetrics();
//            int width = metrics.getBounds().width();
//            int height = metrics.getBounds().height();
//        }
    }

    /**
     * 返回屏幕宽度
     */
    fun getScreenWidth(activity: Activity?): Int {
        return activity?.resources?.displayMetrics?.widthPixels ?: 0
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//            WindowMetrics metrics = activity.getWindowManager().getCurrentWindowMetrics();
//            int width = metrics.getBounds().width();
//            int height = metrics.getBounds().height();
//        }
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    // region 底部虚拟按键栏的高度
    fun getNavigationBarHeight(activity: Activity): Int {
        return if (!isNavigationBarExist(activity)) 0 else getNavigationBarHeightWithoutJudge(
            activity
        )
    }

    fun getNavigationBarHeightWithoutJudge(activity: Activity): Int {
        val metrics = DisplayMetrics()
        //这个方法获取可能不是真实屏幕的高度
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        //获取当前屏幕的真实高度
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }

    /**
     * 判断是否是全面屏
     * 目前仅通过屏幕尺寸比进行粗略判断是否为全面屏
     */
    @Volatile
    private var mHasCheckAllScreen = false

    @Volatile
    private var mIsAllScreenDevice = false

    private fun isAllScreenDevice(context: Context): Boolean {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice
        }
        mHasCheckAllScreen = true
        mIsAllScreenDevice = false
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        val width: Float
        val height: Float
        if (point.x < point.y) {
            width = point.x.toFloat()
            height = point.y.toFloat()
        } else {
            width = point.y.toFloat()
            height = point.x.toFloat()
        }
        if (height / width >= 1.97f) {
            mIsAllScreenDevice = true
        }
        return mIsAllScreenDevice
    }

    /**
     * 适配全面屏的判断（如果不是全面屏的手机进行判断始终返回true）
     * 该方法需要在View完全被绘制出来之后调用，否则判断不了
     * 在比如 onWindowFocusChanged（）方法中可以得到正确的结果
     */
    fun isNavigationBarExist(@NonNull activity: Activity): Boolean {
        if (!isAllScreenDevice(activity)) return true
        val vp = activity.window.decorView as ViewGroup
        for (i in 0 until vp.childCount) {
            vp.getChildAt(i).context.packageName
            if (vp.getChildAt(i).id != View.NO_ID && "navigationBarBackground" == activity.resources.getResourceEntryName(
                    vp.getChildAt(i).id
                )
            ) {
                return true
            }
        }
        return false
    }
    // endregion

    // endregion
    /**
     * 获取软件盘的高度
     */
    fun getSupportSoftInputHeight(activity: Activity): Int {
        val r = Rect()
        /*
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        //获取屏幕的高度
        activity.window.decorView.getWindowVisibleDisplayFrame(r)
        val screenHeight = activity.window.decorView.rootView.height
        //        int screenHeight = GenericTools.getScreenHeight(mActivity);
        //计算软件盘的高度
        var softInputHeight = screenHeight - r.bottom

        /*
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
        // 传入的HomeActivity不包含虚拟导航栏高度，因此暂时屏蔽
        softInputHeight -= getNavigationBarHeight(activity)
        if (softInputHeight < 0) {
            //不返回负数
            softInputHeight = 0
        }
        return softInputHeight
    }

    /**
     * 获取设备像素
     *
     * @return int[] index 0宽,index 1 高
     */
    fun getDevicePixel(activity: Activity): IntArray? {
        val px = IntArray(2)
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)

        // 获得手机的宽度和高度像素单位为px
        px[0] = dm.widthPixels
        px[1] = dm.heightPixels
        return px
    }

    /**
     * 获取设备密度系数
     */
    fun getDensity(activity: Activity): Float {
        val dm = DisplayMetrics()
        activity.window.windowManager.defaultDisplay.getMetrics(dm)
        return dm.density
    }

    /**
     * dp转px
     */
    fun dip2px(context: Context?, dipValue: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * px转dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     */
    fun sp2px(context: Context?, spValue: Float): Int {
        return if (context == null) 0
        else {
            val fontScale = context.resources.displayMetrics.scaledDensity
            (spValue * fontScale + 0.5f).toInt()
        }
    }
}