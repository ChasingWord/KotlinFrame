package com.hebao.testkotlin.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.FragmentFirstBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.shrimp.base.utils.ObjectCacheUtil
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnRefreshListener {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var objectCacheUtil:ObjectCacheUtil

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonFirst.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                objectCacheUtil.save("key", "say hello")
                objectCacheUtil.save("key_int", 1)
            }
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setEnableLoadMoreWhenContentNotFull(true)

        objectCacheUtil = ObjectCacheUtil(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Dispatchers.Main){
                binding.refreshLayout.finishRefresh(false)
            }
        }
    }
}