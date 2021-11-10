package com.hebao.testkotlin.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.hebao.testkotlin.databinding.FragmentSecondBinding
import com.shrimp.base.adapter.viewpager.FragmentPagerWithTitlesAdapter
import com.shrimp.base.utils.GenericTools
import com.shrimp.base.utils.ObjectCacheUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var objectCacheUtil: ObjectCacheUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            lifecycleScope.launch {
                objectCacheUtil.save("key", "say hello too")
            }
        }

        val fragmentList = ArrayList<Fragment>()
        var fragment = ViewPagerFragment()
        fragmentList.add(fragment)
        fragment = ViewPagerFragment()
        fragmentList.add(fragment)
        fragment = ViewPagerFragment()
        fragmentList.add(fragment)
        binding.viewPager.adapter = FragmentPagerWithTitlesAdapter(
            childFragmentManager,
            fragmentList,
            arrayOf("1", "2", "3")
        )
        binding.tabLayout.setTabWidthPx(GenericTools.getScreenWidth(activity)/3f)
        binding.tabLayout.setViewPager(binding.viewPager)
        objectCacheUtil = ObjectCacheUtil(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}