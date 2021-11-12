package com.shrimp.base.view

import android.content.Context
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
 */
abstract class BaseFragment<T : BaseFragmentViewModel, D : ViewDataBinding> : Fragment() {

    protected var oneClickUtil = OneClickUtil()

    // 由ViewModel.isShow进行监听控制显示与隐藏
    private lateinit var dialog: ProgressDialog
    private var showLoadingTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())

    private var isPause = false
    private var hidden = false
    // 判断是否销毁了dataBinding，避免销毁之后再进行使用
    protected var isDestroyView = false

    private val lifeCycleListeners: ArrayList<IFragmentLifeCycleListener> = ArrayList()
    protected lateinit var baseViewModel: T
    private var _dataBinding: D? = null

    // This property is only valid between onCreateView and onDestroyView.
    protected val dataBinding: D = _dataBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseViewModel.onAttach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _dataBinding = inflateDataBinding(inflater, container)
        baseViewModel.onCreateView()
        dialog = ProgressDialog()
        dialog.isCancelable = true

        baseViewModel = ViewModelProvider(this).get(getViewModelClass())
        baseViewModel.dialogShow.observe(viewLifecycleOwner) { isShow ->
            if (isShow)
                showLoading()
            else
                hideLoading()
        }
        baseViewModel.onCreate()
        baseViewModel.handleBundle(arguments)
        return dataBinding.root
    }

    abstract fun inflateDataBinding(inflater: LayoutInflater, container: ViewGroup?): D

    abstract fun getViewModelClass(): Class<T>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initDataObserve()
        baseViewModel.loadingData()
    }

    abstract fun initView()

    abstract fun initDataObserve()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseViewModel.onActivityCreated()
    }

    override fun onStart() {
        super.onStart()
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onStart(this)
        }
        baseViewModel.onStart()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.hidden = hidden
        for (lifeCycleListener in lifeCycleListeners) {
            if (hidden)
                lifeCycleListener.onPause(this)
            else
                lifeCycleListener.onResume(this)
        }
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
        isPause = true
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

    override fun onDestroyView() {
        isDestroyView = true
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onDestroyView(this)
        }
        baseViewModel.onDestroyView()
        super.onDestroyView()
        _dataBinding = null
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onDestroy(this)
        }
        lifeCycleListeners.clear()
        baseViewModel.onDestroy()
        dialog.onDestroy()
        super.onDestroy()
    }

    override fun onDetach() {
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onDetach(this)
        }
        baseViewModel.onDetach()
        super.onDetach()
    }

    fun addLifeCycleListener(lifeCycleListener: IFragmentLifeCycleListener) {
        lifeCycleListeners.add(lifeCycleListener)
    }

    fun removeLifeCycleListener(lifeCycleListener: IFragmentLifeCycleListener) {
        lifeCycleListeners.remove(lifeCycleListener)
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