package com.hebao.testkotlin.view.sub

import com.hebao.testkotlin.R
import com.shrimp.base.adapter.recycler.AbstractMultiItemTypeSupport
import com.shrimp.network.entity.res.Tags

/**
 * Created by chasing on 2021/10/26.
 */
class SecondMultiItemTypeSupport : AbstractMultiItemTypeSupport<Tags>() {

    override fun getItemType(t: Tags, position: Int): Int = when {
        t.TagId <= 400 -> 0
        else -> 1
    }

    override fun getLayoutId(type: Int): Int = when (type) {
        0 -> R.layout.item_second
        else -> R.layout.item_second_2
    }

    override fun isConvert(t: Tags, position: Int): Boolean = true
}