package com.shrimp.base.view

/**
 * Created by chasing on 2021/11/12.
 */
abstract class AbstractFragmentLifeCycleListener : IFragmentLifeCycleListener {
    override fun onStart() {}
    override fun onResume() {}
    override fun onPause() {}
    override fun onStop() {}
    override fun onDestroyView() {}
    override fun onDestroy() {}
    override fun onDetach() {}
}