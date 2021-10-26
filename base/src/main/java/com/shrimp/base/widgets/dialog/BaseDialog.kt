package com.shrimp.base.widgets.dialog

import android.R
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * Created by chasing on 2021/10/26.
 */
open abstract class BaseDialog : DialogFragment() {
    protected var mContentView: View? = null
    private var dismissListener: DismissListener? = null

    open fun BaseDialog() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            setStyle(STYLE_NO_TITLE, 0)
        }
        getExtraParams()
    }

    override fun onDestroy() {
        mContentView = null
        super.onDestroy()
    }

    open fun setDismissListener(dismissListener: DismissListener?) {
        this.dismissListener = dismissListener
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            window!!.setBackgroundDrawableResource(R.color.transparent)
            val params = window.attributes
            params.gravity = Gravity.CENTER
            params.dimAmount = 0.35f //设置透明度
            window.attributes = params
            dialog.setCanceledOnTouchOutside(false)
        }
        initComponents(getContentView())
    }

    @SuppressLint("CommitTransaction")
    override fun show(manager: FragmentManager, tag: String?) {
        val fragment = manager.findFragmentByTag(tag)
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        show(manager.beginTransaction(), tag)
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        transaction.add(this, tag)
        return transaction.commitAllowingStateLoss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (dismissListener != null) {
            dismissListener!!.dismiss()
        }
    }

    protected fun buildArguments(): Bundle {
        var b = arguments
        if (b == null) {
            b = Bundle()
        }
        return b
    }

    protected fun getContentView(): View? {
        return mContentView
    }

    protected abstract fun getExtraParams()

    protected abstract fun initComponents(view: View?)

    interface DismissListener {
        fun dismiss()
    }
}