package com.hebao.testkotlin.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.FragmentFirstBinding
import com.hebao.testkotlin.view.chart.ChartActivity
import com.hebao.testkotlin.view.datastore.TestDatastoreActivity
import com.hebao.testkotlin.view.meituan.MeiTuanActivity
import com.hebao.testkotlin.view.sub.SecondActivity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.shrimp.base.utils.StatusBarUtil
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnRefreshListener {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            StatusBarUtil.setColorDiff(it,
                ContextCompat.getColor(it, R.color.purple_200))
        }

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.chart.setOnClickListener {
            ChartActivity.start(requireContext())
        }
        binding.textviewFirst.setOnClickListener {
            SecondActivity.start(requireContext())
        }
        binding.textviewSecond.setOnClickListener {
            TestDatastoreActivity.start(requireContext())
        }
        binding.textviewMeituan.setOnClickListener {
            MeiTuanActivity.start(requireContext())
        }
        binding.testMotion.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_TestMotion)
        }
        binding.test3D.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_test3DModelFragment)
        }

        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setEnableLoadMoreWhenContentNotFull(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Dispatchers.Main) {
                binding.refreshLayout.finishRefresh(false)
            }
        }
    }
}