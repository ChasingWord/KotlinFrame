package com.shrimp.base.view

import androidx.fragment.app.Fragment

/**
 * Created by chasing on 2021/11/12.
 */
interface IFragmentLifeCycleListener {
    fun onStart(fragment: Fragment)
    fun onResume(fragment: Fragment)
    fun onPause(fragment: Fragment)
    fun onStop(fragment: Fragment)
    fun onDestroyView(fragment: Fragment)
    fun onDestroy(fragment: Fragment)
    fun onDetach(fragment: Fragment)
}