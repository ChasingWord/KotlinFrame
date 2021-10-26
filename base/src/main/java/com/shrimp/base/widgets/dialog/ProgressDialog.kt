package com.shrimp.base.widgets.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.shrimp.base.R

/**
 * Created by chasing on 2021/10/26.
 */
class ProgressDialog : BaseDialog() {

    private var mMessage: String? = null
    var isShowing = false
        private set

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContentView = inflater.inflate(R.layout.shrimp_view_loading, container, false)
        setDismissListener(object : DismissListener {
            override fun dismiss() {
                isShowing = false
            }
        })
        return mContentView
    }

    override fun getExtraParams() {
        val bundle = arguments ?: return
        mMessage = bundle.getString("message")
    }


    fun setMessage(text: String?) {
        val bundle = buildArguments()
        bundle.putString("message", text)
        arguments = bundle
    }

    override fun initComponents(view: View?) {
        val tvMessage = view?.findViewById<TextView>(R.id.define_porgress_tvMessage)
        if (!TextUtils.isEmpty(mMessage)) {
            tvMessage?.text = mMessage
        }
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        val params = this.dialog!!.window!!.attributes
        params.gravity = Gravity.CENTER
        //        params.gravity = Gravity.TOP;
//        params.y = GenericTools.dip2px(getActivity(), 50);
        params.dimAmount = 0.5f
        this.dialog!!.window!!.attributes = params
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        if (isShowing) return 0
        try {
            isShowing = true
            return super.show(transaction, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    override fun dismiss() {
        if (!isShowing) return
        try {
            super.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}