package com.hebao.testkotlin.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.FragmentSecondBinding
import com.shrimp.base.adapter.viewpager.FragmentPagerWithTitlesAdapter
import com.shrimp.base.utils.GenericTools
import com.shrimp.base.utils.ObjectCacheUtil
import com.shrimp.base.view.BaseFragment

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : BaseFragment<SecondFragmentViewModel, FragmentSecondBinding>() {

    private lateinit var objectCacheUtil: ObjectCacheUtil

    override fun inflateDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSecondBinding = FragmentSecondBinding.inflate(inflater, container, false)

    override fun getViewModelClass(): Class<SecondFragmentViewModel> = SecondFragmentViewModel::class.java

    override fun initView() {
        dataBinding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        val fragmentList = ArrayList<Fragment>()
        var fragment = ViewPagerFragment()
        fragmentList.add(fragment)
        fragment = ViewPagerFragment()
        fragmentList.add(fragment)
        fragment = ViewPagerFragment()
        fragmentList.add(fragment)
        dataBinding.viewPager.adapter = FragmentPagerWithTitlesAdapter(
            childFragmentManager,
            fragmentList,
            arrayOf("1", "2", "3")
        )
        dataBinding.tabLayout.setTabWidthPx(GenericTools.getScreenWidth(activity) / 3f)
        dataBinding.tabLayout.setViewPager(dataBinding.viewPager)
        objectCacheUtil = ObjectCacheUtil(requireContext())
    }

    override fun initDataObserve() {

    }
}