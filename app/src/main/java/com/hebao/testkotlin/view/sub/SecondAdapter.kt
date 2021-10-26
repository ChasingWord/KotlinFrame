package com.hebao.testkotlin.view.sub

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ItemSecond2Binding
import com.hebao.testkotlin.databinding.ItemSecondBinding
import com.shrimp.base.adapter.BaseRecyclerAdapter

/**
 * Created by chasing on 2021/10/25.
 */
class SecondAdapter : BaseRecyclerAdapter<String> {

    constructor(context: Context) : super(context, R.layout.item_second) {
    }

    constructor(context: Context, boolean: Boolean) : super(context, SecondMultiItemTypeSupport()) {
    }

    override fun convert(viewType: Int, dataBinding: ViewDataBinding, item: String) {
        if (dataBinding is ItemSecondBinding) {
            dataBinding.itemSecondText.text = item
        } else if (dataBinding is ItemSecond2Binding) {
            dataBinding.itemSecondText.text = item
        }
    }
}