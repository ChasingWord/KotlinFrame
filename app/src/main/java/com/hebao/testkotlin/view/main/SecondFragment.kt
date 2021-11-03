package com.hebao.testkotlin.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.FragmentSecondBinding
import com.shrimp.base.adapter.viewpager.FragmentPagerWithTitlesAdapter

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }


        val fragmentList = ArrayList<Fragment>()
        var fragment = ViewPagerFragment()
        fragmentList.add(fragment)
        fragment = ViewPagerFragment()
        fragmentList.add(fragment)
        fragment = ViewPagerFragment()
        fragmentList.add(fragment)
        binding.viewPager.adapter=FragmentPagerWithTitlesAdapter(childFragmentManager, fragmentList, arrayOf("1", "2", "3"))
        binding.tabLayout.setViewPager(binding.viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}