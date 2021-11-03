package com.shrimp.base.adapter.recycler

import android.view.View
import androidx.databinding.ViewDataBinding

/**
 * Created by chasing on 2021/10/26.
 */
abstract class AbstractMultiItemTypeSupport<T> : IRecyclerMultiItemTypeSupport<T> {
    override var dataBindingMap:MutableMap<View, ViewDataBinding> =HashMap()

    override fun getDataBinding(view: View): ViewDataBinding? = dataBindingMap[view]
}