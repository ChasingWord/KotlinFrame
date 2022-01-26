package com.shrimp.base.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.shrimp.base.R

/**
 * 注意：要在setContentView之后调用
 * Android5.0以下需要特殊处理才能适配修改状态栏颜色
 * Android5.0开始可以修改状态栏颜色
 * Android6.0开始可以修改状态栏字体颜色
 */
object StatusBarUtil {
    // <editor-fold desc="设置状态栏背景颜色--为了适配5.0以下的">
    private val FAKE_STATUS_BAR_VIEW_ID: Int = R.id.statusbarutil_fake_status_bar_view

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    fun setColorDiff(activity: Activity, @ColorInt color: Int) {
        setTransparentStatusBar(activity)
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        val fakeStatusBarView = contentView.findViewById<View>(
            FAKE_STATUS_BAR_VIEW_ID
        )
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.visibility == View.GONE) {
                fakeStatusBarView.visibility = View.VISIBLE
            }
            fakeStatusBarView.setBackgroundColor(color)
        } else {
            contentView.addView(createStatusBarView(activity, color))
        }
        setRootView(activity, true)
    }

    fun setColorDiffThemeWhite(activity: Activity, @ColorInt color: Int) {
        setTransparentStatusBar(activity, false)
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        val fakeStatusBarView = contentView.findViewById<View>(FAKE_STATUS_BAR_VIEW_ID)
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.visibility == View.GONE) {
                fakeStatusBarView.visibility = View.VISIBLE
            }
            fakeStatusBarView.setBackgroundColor(color)
        } else {
            contentView.addView(createStatusBarView(activity, color))
        }
        setRootView(activity, true)
    }

    fun hideCustomStatusBarView(activity: Activity) {
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        val fakeStatusBarView = contentView.findViewById<View>(
            FAKE_STATUS_BAR_VIEW_ID
        )
        if (fakeStatusBarView != null) {
            fakeStatusBarView.visibility = View.GONE
        }
        setRootView(activity, false)
    }
    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    private fun createStatusBarView(
        activity: Activity,
        @ColorInt color: Int,
        alpha: Int = 0
    ): View {
        // 绘制一个和状态栏一样高的矩形
        val statusBarView = View(activity)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            getStatusBarHeight(activity)
        )
        statusBarView.layoutParams = params
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha))
        statusBarView.id = FAKE_STATUS_BAR_VIEW_ID
        return statusBarView
    }

    /**
     * 设置根布局参数
     * fitsSystemWindows 介绍
     * 根据官方文档，如果某个View 的fitsSystemWindows 设为true，那么该View的padding属性将由系统设置，用户在布局文件中设置的
     * padding会被忽略。系统会为该View设置一个paddingTop，值为statusbar的高度。fitsSystemWindows默认为false。
     * 重要说明：
     * 只有将statusbar设为透明，或者界面设为全屏显示（设置View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN flag)时，
     * fitsSystemWindows才会起作用。不然statusbar的空间轮不到用户处理，这时会由ContentView的父控件处理，
     * 如果用HierarchyView 工具查看，将会看到，ContentView的父控件的paddingTop将会被设置。
     * 如果多个view同时设置了fitsSystemWindows，只有第一个会起作用。这是一般情况，后面会介绍特殊情况。
     */
    private fun setRootView(activity: Activity, fitsSystemWindows: Boolean) {
        val parent = activity.findViewById<ViewGroup>(android.R.id.content)
        var i = 0
        val count = parent.childCount
        while (i < count) {
            val childView = parent.getChildAt(i)
            if (childView is ViewGroup) {
                childView.setFitsSystemWindows(fitsSystemWindows)
                childView.clipToPadding = true
            }
            i++
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private fun calculateStatusColor(@ColorInt color: Int, alpha: Int): Int {
        if (alpha == 0) {
            return color
        }
        val a = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }

    // </editor-fold>
    // <editor-fold desc="切换状态栏字体颜色（黑白）,默认是白色">
    //1.对于miui，适配6.0以下
    //字体颜色是否设置为黑色。注：只是设置字体颜色，不进行设置背景栏颜色
    @SuppressLint("PrivateApi")
    private fun setMiuiStatusBarDarkMode(activity: Activity, darkmode: Boolean): Boolean {
        val clazz: Class<out Window> = activity.window.javaClass
        try {
            // 在旧的MIUI版本
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            extraFlagField.invoke(activity.window, if (darkmode) darkModeFlag else 0, darkModeFlag)
            return true
        } catch (ignored: Exception) {
        }
        return false
    }

    //2.对于flyme，适配6.0以下
    //设置成白色的背景，字体颜色为黑色。
    private fun setMeizuStatusBarDarkIcon(activity: Activity, dark: Boolean): Boolean {
        try {
            val lp = activity.window.attributes
            val darkFlag = WindowManager.LayoutParams::class.java
                .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java
                .getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            value = if (dark) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            activity.window.attributes = lp
            return true
        } catch (ignored: Exception) {
        }
        return false
    }

    // </editor-fold>
    //设置全屏状态--隐藏StatusBar
    fun setFullScreen(aty: Activity) {
        //5.0及以上
        val localLayoutParams = aty.window.attributes
        localLayoutParams.flags =
            WindowManager.LayoutParams.FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        //适配刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            localLayoutParams.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            var systemUiVisibility = aty.window.decorView.systemUiVisibility
            val flags =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            systemUiVisibility = systemUiVisibility or flags
            aty.window.decorView.systemUiVisibility = systemUiVisibility
        }
    }

    // 设置布局全屏顶上StatusBar，设置statusBar的背景颜色为透明且字体为黑色系
    fun setTransparentStatusBar(aty: Activity) {
        setTransparentStatusBar(aty, true)
    }

    // 设置布局全屏顶上StatusBar，设置statusBar的背景颜色为透明且字体为黑色系
    // activity的顶层布局添加android:fitsSystemWindows="true"，否则软键盘的弹窗会受到影响
    // android:fitsSystemWindows="true"会使顶层布局向上留白留出statusBar的空间
    // 动态设置false的时候还需要清楚paddingTop：
    // view.setFitsSystemWindows(false);
    // view.setPadding(view.getPaddingLeft(), 0, view.getPaddingRight(), view.getPaddingBottom());
    fun setTransparentStatusBar(aty: Activity, isDarkMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //6.0及以上
            val window = aty.window
            if (isDarkMode) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            window.statusBarColor = ContextCompat.getColor(aty, R.color.transparent)
        } else  {  //5.0及以上
            val window = aty.window
            //切换状态栏字体颜色
            if (!setMiuiStatusBarDarkMode(aty, isDarkMode)) { //尝试设置小米系统
                if (!setMeizuStatusBarDarkIcon(aty, isDarkMode)) { //尝试设置魅族系统
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                }
            }
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = ContextCompat.getColor(aty, R.color.transparent)
        }
    }
}