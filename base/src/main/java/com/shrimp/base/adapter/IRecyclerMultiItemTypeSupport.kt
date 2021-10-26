package com.shrimp.base.adapter

import android.view.View
import androidx.databinding.ViewDataBinding

/**
 * Created by chasing on 2021/10/25.
 */
internal interface IRecyclerMultiItemTypeSupport<T> {
    val dataBindingMap:MutableMap<View, ViewDataBinding>

    fun getItemType(t: T, position: Int): Int
    fun getLayoutId(type: Int):Int
    fun getDataBinding(view: View): ViewDataBinding
    /**
     * 是否需要处理itemView
     *
     * @param t        数据
     * @param position 位置
     * @return 是否进入convert处理
     */
    fun isConvert(t: T, position: Int): Boolean
}