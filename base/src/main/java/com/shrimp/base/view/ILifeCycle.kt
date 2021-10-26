package com.shrimp.base.view

/**
 * Created by chasing on 2021/10/19.
 */
interface ILifeCycle {
    fun onCreate()
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
}