package com.hebao.testkotlin.view.meituan

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ItemStringBinding
import com.shrimp.base.adapter.recycler.BaseRecyclerAdapter
import com.shrimp.base.utils.ItemTouchCallBack
import com.shrimp.base.utils.getViewWidth
import java.util.*

/**
 * Created by chasing on 2022/2/11.
 */
class FirstRcvAdapter(context: Context) :
    BaseRecyclerAdapter<String>(context, R.layout.item_string),
    ItemTouchCallBack.OnItemTouchListener {
    override fun convert(viewType: Int, dataBinding: ViewDataBinding, item: String) {
        dataBinding as ItemStringBinding
        dataBinding.itemString.text = item
    }

    override fun onMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(getAll(), i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(getAll(), i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        if (fromPosition > toPosition) notifyItemRangeChanged(
            toPosition,
            fromPosition - toPosition + 1
        ) else notifyItemRangeChanged(
            fromPosition,
            toPosition - fromPosition + 1
        )
    }

    override fun onSwiped(position: Int) {
        remove(position)
    }
}