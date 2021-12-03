package com.shrimp.base.view

import androidx.fragment.app.Fragment

/**
 * Created by chasing on 2021/11/12.
 */
abstract class AbstractFragmentLifeCycleListener : IFragmentLifeCycleListener {
    override fun onStart(fragment: Fragment) {}
    override fun onResume(fragment: Fragment) {}
    override fun onPause(fragment: Fragment) {}
    override fun onStop(fragment: Fragment) {}
    override fun onDestroyView(fragment: Fragment) {}
    override fun onDestroy(fragment: Fragment) {}
    override fun onDetach(fragment: Fragment) {}
}