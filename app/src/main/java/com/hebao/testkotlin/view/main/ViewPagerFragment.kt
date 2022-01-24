package com.hebao.testkotlin.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hebao.testkotlin.databinding.FragmentViewPageBinding

/**
 * Created by chasing on 2021/11/3.
 */
class ViewPagerFragment : Fragment() {

    private var _binding: FragmentViewPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPageBinding.inflate(inflater, container, false)
        return binding.root
    }
}