package com.hebao.testkotlin.view.sub

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.hebao.testkotlin.databinding.ItemSecond2Binding
import com.hebao.testkotlin.databinding.ItemSecondBinding
import com.shrimp.base.adapter.recycler.BaseRecyclerAdapter

/**
 * Created by chasing on 2021/10/25.
 */
class SecondAdapter(context: Context) :
    BaseRecyclerAdapter<String>(context, SecondMultiItemTypeSupport()) {

    override fun convert(viewType: Int, dataBinding: ViewDataBinding, item: String) {
        if (dataBinding is ItemSecondBinding) {
            dataBinding.itemSecondText.text = item
        } else if (dataBinding is ItemSecond2Binding) {
            dataBinding.itemSecondText.text = item
        }
    }
}