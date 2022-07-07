package com.hebao.testkotlin.view.sub

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.hebao.testkotlin.databinding.ItemSecond2Binding
import com.hebao.testkotlin.databinding.ItemSecondBinding
import com.shrimp.base.adapter.recycler.BaseRecyclerAdapter
import com.shrimp.network.entity.res.Tags

/**
 * Created by chasing on 2021/10/25.
 */
class SecondAdapter(context: Context) :
    BaseRecyclerAdapter<Tags>(context, SecondMultiItemTypeSupport()) {

    override fun convert(position:Int, viewType: Int, dataBinding: ViewDataBinding, item: Tags) {
        if (dataBinding is ItemSecondBinding) {
            dataBinding.itemSecondText.text = item.Name
        } else if (dataBinding is ItemSecond2Binding) {
            dataBinding.itemSecondText.text = item.Name
        }
    }
}