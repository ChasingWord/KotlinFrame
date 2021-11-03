package com.hebao.testkotlin.view.sub

import com.hebao.testkotlin.R
import com.shrimp.base.adapter.recycler.AbstractMultiItemTypeSupport

/**
 * Created by chasing on 2021/10/26.
 */
class SecondMultiItemTypeSupport : AbstractMultiItemTypeSupport<String>() {

    override fun getItemType(t: String, position: Int): Int = when (t) {
        "0", "1", "2" -> 0
        else -> 1
    }

    override fun getLayoutId(type: Int): Int = when (type) {
        0 -> R.layout.item_second
        else -> R.layout.item_second_2
    }

    override fun isConvert(t: String, position: Int): Boolean = true
}