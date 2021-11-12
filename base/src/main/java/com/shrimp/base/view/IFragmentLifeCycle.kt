package com.shrimp.base.view

/**
 * Created by chasing on 2021/11/12.
 */
interface IFragmentLifeCycle {
    fun onAttach() {}
    fun onCreate()
    fun onCreateView()
    fun onActivityCreated()
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroyView()
    fun onDestroy()
    fun onDetach()
}