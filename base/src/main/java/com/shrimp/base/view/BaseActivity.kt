package com.shrimp.base.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
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
abstract class BaseActivity<VM : BaseViewModel, B : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var context: Activity
    protected var oneClickUtil = OneClickUtil()
    protected var isPause = false

    private lateinit var dialog: ProgressDialog
    private var showLoadingTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())

    protected lateinit var viewModel: VM
    protected lateinit var dataBinding: B

    // 是否切换状态栏
    protected var needChangeStatusBar = true

    // 状态栏是否深色主题
    protected var isStatusBarDarkMode = true

    // 是否全屏模式
    protected var isFullScreen = false

    // 状态栏颜色，当设置透明00000000时，则布局会顶到最上
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        context = this

        dataBinding = inflateDataBinding()
        dataBinding.lifecycleOwner = this
        setContentView(dataBinding.root)

        changeConfig()
        if (needChangeStatusBar) {
            when {
                isFullScreen -> StatusBarUtil.setFullScreen(this)
                statusBarColor == R.color.transparent -> StatusBarUtil.setTransparentStatusBar(
                    this,
                    isStatusBarDarkMode
                )
                isStatusBarDarkMode -> StatusBarUtil.setColorDiff(
                    this,
                    ContextCompat.getColor(this, statusBarColor)
                )
                else -> StatusBarUtil.setColorDiffThemeWhite(
                    this,
                    ContextCompat.getColor(this, statusBarColor)
                )
            }
        }

        dialog = ProgressDialog()
        dialog.isCancelable = true

        viewModel = ViewModelProvider(this).get(getViewModelClass())
        viewModel.dialogShow.observe(this) { isShow ->
            if (isShow)
                showLoading()
            else
                hideLoading()
        }
        viewModel.handleIntent(intent)
        lifecycle.addObserver(viewModel)

        initView()
        initDataObserve()

        viewModel.loadData()
        loadData()
    }

    /**
     * 切换配置，用于修改StatusBar参数等
     */
    open fun changeConfig() {
    }

    /**
     * bindingView
     */
    abstract fun inflateDataBinding(): B

    abstract fun getViewModelClass(): Class<VM>

    /**
     * 初始化视图
     */
    abstract fun initView()

    /**
     * 初始化ViewModel的数据监听
     */
    abstract fun initDataObserve()

    /**
     * 需要使用Activity进行加载数据的情形，例如：加载本地相册需要使用FragmentActivity
     * 其它数据加载写在ViewModel
     */
    open fun loadData() {

    }

    override fun onResume() {
        super.onResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        isPause = true;
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        FixMemLeakUtil.fixLeak(this)
        dialog.onDestroy()
        super.onDestroy()
        dataBinding.unbind()
    }

    private fun showLoading() {
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
    private fun hideLoading() {
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