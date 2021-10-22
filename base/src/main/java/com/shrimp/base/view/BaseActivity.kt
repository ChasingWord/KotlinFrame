package com.shrimp.base.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shrimp.base.utils.ActivityUtil

/**
 * Created by chasing on 2021/10/19.
 */
class BaseActivity : AppCompatActivity() {
    private val lifeCycleListeners: ArrayList<LifeCycleListener> = ArrayList()
    private lateinit var baseViewModel: ViewModel

    companion object {
        //统一判断，避免连续打开两个界面
        fun start(context: Context, clazz: Class<*>?) {
            start(context, Intent(context, clazz))
        }

        fun start(context: Context, intent: Intent) {
            if (intent.component != null && ActivityUtil.oneClickUtil.check(intent.component!!.className)) return
            context.startActivity(intent)
        }

        fun startForResult(activity: Activity, intent: Intent, requestCode: Int) {
            if (intent.component != null && ActivityUtil.oneClickUtil.check(intent.component!!.className)) return
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onStart(this)
        }
    }

    override fun onResume() {
        super.onResume()
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onResume(this)
        }
    }

    override fun onPause() {
        super.onPause()
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onPause(this)
        }
    }

    override fun onStop() {
        super.onStop()
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onStop(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (lifeCycleListener in lifeCycleListeners) {
            lifeCycleListener.onDestroy(this)
        }
        lifeCycleListeners.clear()
    }

    fun addLifeCycleListener(lifeCycleListener: LifeCycleListener) {
        lifeCycleListeners.add(lifeCycleListener)
    }

    fun removeLifeCycleListener(lifeCycleListener: LifeCycleListener) {
        lifeCycleListeners.remove(lifeCycleListener)
    }
}