package com.hebao.testkotlin.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hebao.testkotlin.databinding.FragmentTestMotionBinding
import com.shrimp.base.utils.showToast
import com.shrimp.base.view.BaseFragment

/**
 * Created by chasing on 2022/7/4.
 */
class TestMotionFragment : BaseFragment<TestMotionViewModel, FragmentTestMotionBinding>() {
    override fun inflateDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentTestMotionBinding = FragmentTestMotionBinding.inflate(inflater, container, false)

    override fun getViewModelClass(): Class<TestMotionViewModel> = TestMotionViewModel::class.java

    override fun initView() {
        dataBinding.button.setOnClickListener{
            showToast("hi")
        }
    }

    override fun initDataObserve() {

    }
}