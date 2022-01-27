package com.shrimp.base.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.shrimp.base.utils.OneClickUtil
import com.shrimp.base.widgets.dialog.ProgressDialog

/**
 * Created by chasing on 2021/11/12.
 * 因为系统封装的LifecycleObserve只有
 */
abstract class BaseFragment<VM : BaseFragmentViewModel, B : ViewDataBinding> : Fragment() {

    protected var oneClickUtil = OneClickUtil()

    // 由ViewModel.isShow进行监听控制显示与隐藏
    private lateinit var dialog: ProgressDialog
    private var showLoadingTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())

    private var isPause = false
    private var hidden = false

    // 判断是否销毁了dataBinding，避免销毁之后再进行使用
    protected var isDestroyView = false

    protected lateinit var viewModel: VM
    protected lateinit var dataBinding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(getViewModelClass())
        lifecycle.addObserver(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = inflateDataBinding(inflater, container)
        dataBinding.lifecycleOwner = this

        dialog = ProgressDialog()
        dialog.isCancelable = true

        viewModel.dialogShow.observe(viewLifecycleOwner) { isShow ->
            if (isShow)
                showLoading()
            else
                hideLoading()
        }
        viewModel.handleBundle(arguments)
        return dataBinding.root
    }

    /**
     * bindingView
     */
    abstract fun inflateDataBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun getViewModelClass(): Class<VM>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initDataObserve()

        viewModel.loadingData()
    }

    /**
     * 初始化视图
     */
    abstract fun initView()

    /**
     * 初始化ViewModel的数据监听
     */
    abstract fun initDataObserve()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.hidden = hidden
    }

    override fun onResume() {
        super.onResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    override fun onDestroyView() {
        isDestroyView = true
        super.onDestroyView()
        dataBinding.unbind()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        dialog.onDestroy()
        super.onDestroy()
    }

    private fun showLoading() {
        if (activity?.isFinishing != false) return
        if (dialog.isShowing) return
        val fg: FragmentManager = childFragmentManager
        val ft: FragmentTransaction = fg.beginTransaction()
        dialog.show(ft, javaClass.name)
        showLoadingTime = System.currentTimeMillis()
    }

    /**
     * 如果showLoading的时间与hideLoading的时间相差太接近可能导致在调用dismiss的时候dialog还没有正真显示出来
     * 而dismiss之后dialog才正真显示出来，所以进行时间差判断（如果相差0.3s内就调用hide则延迟处理）
     */
    private fun hideLoading() {
        if (activity?.isFinishing != false && dialog.isShowing) {
            val time = System.currentTimeMillis()
            if (time - showLoadingTime < 300) {
                handler.postDelayed({
                    if (activity?.isFinishing != false) {
                        dialog.dismiss()
                    }
                }, 300 - (time - showLoadingTime))
            } else {
                dialog.dismiss()
            }
        }
    }

    fun isShowing() = !isPause && !hidden
}