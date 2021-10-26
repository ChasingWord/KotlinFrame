package com.shrimp.base.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.shrimp.base.R
import com.shrimp.base.utils.ActivityUtil
import com.shrimp.base.utils.FixMemLeakUtil
import com.shrimp.base.utils.OneClickUtil
import com.shrimp.base.utils.StatusBarUtil
import com.shrimp.base.widgets.dialog.ProgressDialog

/**
 * Created by chasing on 2021/10/19.
 */
abstract class BaseActivity<T : BaseViewModel, D : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var context: Activity
    protected var oneClickUtil = OneClickUtil()
    protected var isPause = false

    private lateinit var dialog: ProgressDialog
    private var showLoadingTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())

    private val lifeCycleListeners: ArrayList<LifeCycleListener> = ArrayList()
    protected lateinit var baseViewModel: T
    protected lateinit var dataBinding: D

    protected var needChangeStatusBar = true
    protected var needStatusBarDarkMode = true
    protected var statusBarColor = R.color.ffffff

    companion object {
        //统一判断，避免连续打开两个界面
        fun start(context: Context, clazz: Class<*>?) {
            start(context, Intent(context, clazz))
        }

        fun start(context: Context, intent: Intent) {
            if (intent.component != null && ActivityUtil.oneClickUtil.check(intent.component?.className)) return
            context.startActivity(intent)
        }

        fun startForResult(activity: Activity, intent: Intent, requestCode: Int) {
            if (intent.component != null && ActivityUtil.oneClickUtil.check(intent.component?.className)) return
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        dialog = ProgressDialog()
        dialog.isCancelable = true
        changeConfig()

        if (needChangeStatusBar) {
            when {
                statusBarColor == R.color.transparent -> StatusBarUtil.setTransparentStatusBar(
                    this,
                    needStatusBarDarkMode
                )
                needStatusBarDarkMode -> StatusBarUtil.setColorDiff(
                    this,
                    ContextCompat.getColor(this, statusBarColor)
                )
                else -> StatusBarUtil.setColorDiffThemeWhite(
                    this,
                    ContextCompat.getColor(this, statusBarColor)
                )
            }
        }

        dataBinding = initContentView()
        dataBinding.lifecycleOwner = this
        setContentView(dataBinding.root)

        baseViewModel = ViewModelProvider(this).get(initViewModel())
        baseViewModel.dialogShow.observe(this) { isShow ->
            if (isShow)
                showLoading()
            else
                hideLoading()
        }
        baseViewModel.onCreate()
        baseViewModel.handleIntent(intent)
        baseViewModel.loadingData()

        initView()
    }

    /**
     * 切换配置，用于修改StatusBar参数等
     */
    open fun changeConfig() {
    }

    abstract fun initViewModel(): Class<T>

    /**
     * bindingView
     */
    abstract fun initContentView(): D

    /**
     * 初始化视图
     */
    abstract fun initView()

    override fun onStart() {
        super.onStart()
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onStart(this)
        }
        baseViewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        isPause = false
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onResume(this)
        }
        baseViewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        isPause = true;
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onPause(this)
        }
        baseViewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onStop(this)
        }
        baseViewModel.onStop()
    }

    override fun onDestroy() {
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onDestroy(this)
        }
        lifeCycleListeners.clear()
        baseViewModel.onDestroy()
        FixMemLeakUtil.fixLeak(this)
        dialog.onDestroy()
        super.onDestroy()
    }

    fun addLifeCycleListener(lifeCycleListener: LifeCycleListener) {
        lifeCycleListeners.add(lifeCycleListener)
    }

    fun removeLifeCycleListener(lifeCycleListener: LifeCycleListener) {
        lifeCycleListeners.remove(lifeCycleListener)
    }

    fun showLoading() {
        if (isFinishing) return
        if (dialog.isShowing) return
        val fg: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fg.beginTransaction()
        dialog.show(ft, javaClass.name)
        showLoadingTime = System.currentTimeMillis()
    }

    /**
     * 如果showLoading的时间与hideLoading的时间相差太接近可能导致在调用dismiss的时候dialog还没有正真显示出来
     * 而dismiss之后dialog才正真显示出来，所以进行时间差判断（如果相差0.3s内就调用hide则延迟处理）
     */
    fun hideLoading() {
        if (!isFinishing && dialog.isShowing) {
            val time = System.currentTimeMillis()
            if (time - showLoadingTime < 300) {
                handler.postDelayed({
                    if (!isFinishing) {
                        dialog.dismiss()
                    }
                }, 300 - (time - showLoadingTime))
            } else {
                dialog.dismiss()
            }
        }
    }
}