package com.shrimp.base.adapter.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by chasing on 2021/10/25.
 */
abstract class BaseRecyclerAdapter<T>(var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data: MutableList<T> = ArrayList()
    private var layoutId: Int = 0
    private lateinit var dataBinding: ViewDataBinding
    private var isMultiItemType = false
    private lateinit var recyclerMultiItemTypeSupport: IRecyclerMultiItemTypeSupport<T>

    private var isLongClick = false
    private var itemClickListener: ItemClickListener? = null
    private var itemLongClickListener: ItemLongClickListener? = null
    private var onItemLongClickRelease: ItemLongClickReleaseListener? = null

    constructor(
        context: Context,
        layoutId: Int
    ) : this(context) {
        this.layoutId = layoutId
    }

    constructor(
        context: Context,
        iRecyclerMultiItemTypeSupport: AbstractMultiItemTypeSupport<T>
    ) : this(context) {
        this.isMultiItemType = true
        this.recyclerMultiItemTypeSupport = iRecyclerMultiItemTypeSupport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isMultiItemType) {
            val layoutId1 = recyclerMultiItemTypeSupport.getLayoutId(viewType)
            val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context),
                layoutId1,
                parent,
                false
            )
            recyclerMultiItemTypeSupport.dataBindingMap[dataBinding.root] = dataBinding
            object : RecyclerView.ViewHolder(dataBinding.root) {}
        } else {
            dataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutId,
                parent,
                false
            )
            object : RecyclerView.ViewHolder(dataBinding.root) {}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val interrupt: Boolean = onBind(holder, position)
        if (interrupt) return

        val t: T = data[position]
        val itemViewType = getItemViewType(position)
        val dataBinding =
            if (isMultiItemType) recyclerMultiItemTypeSupport.getDataBinding(holder.itemView)
            else this.dataBinding
        if (dataBinding != null)
            convert(itemViewType, dataBinding, t)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) onBindViewHolder(holder, position)
        else {
            val interrupt = onBind(holder, position)
            if (interrupt) return
            val t: T = data[position]
            val itemViewType = getItemViewType(position)
            val dataBinding =
                if (isMultiItemType) recyclerMultiItemTypeSupport.getDataBinding(holder.itemView)
                else this.dataBinding

            // 移除相同的payload，避免重复处理
            val truePayLoads: MutableList<String> = ArrayList()
            val waitDeal: MutableList<Any> = ArrayList()
            for (payload in payloads) {
                if (truePayLoads.contains(payload.toString())) continue
                truePayLoads.add(payload.toString())
                waitDeal.add(payload)
            }
            if (dataBinding != null)
                convertPart(itemViewType, dataBinding, t, waitDeal)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onBind(viewHolder: RecyclerView.ViewHolder, position: Int): Boolean {
        viewHolder.itemView.setOnClickListener { onItemClick(position) }
        viewHolder.itemView.setOnLongClickListener {
            isLongClick = true
            onItemLongClick(position)
        }
        viewHolder.itemView.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {
                if (isLongClick) {
                    onItemLongClickRelease?.onItemLongClickRelease(position)
                }
                isLongClick = false
            }
            false
        }
        val t: T = data[position]
        return isMultiItemType && !recyclerMultiItemTypeSupport.isConvert(t, position)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return if (isMultiItemType) recyclerMultiItemTypeSupport.getItemType(
            data[position],
            position
        )
        else super.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    protected abstract fun convert(viewType: Int, dataBinding: ViewDataBinding, item: T)

    protected open fun convertPart(
        viewType: Int,
        dataBinding: ViewDataBinding, item: T,
        payloads: MutableList<Any>
    ) {
    }

    // region 点击事件
    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    interface ItemLongClickListener {
        fun onItemLongClick(position: Int): Boolean
    }

    interface ItemLongClickReleaseListener {
        fun onItemLongClickRelease(position: Int)
    }

    private fun onItemClick(position: Int) {
        if (position < data.size) {
            itemClickListener?.onItemClick(position)
        }
    }

    private fun onItemLongClick(position: Int): Boolean {
        return if (position < data.size)
            itemLongClickListener?.onItemLongClick(position) ?: false
        else false
    }

    fun setItemClickListener(clickListener: ItemClickListener) {
        this.itemClickListener = clickListener
    }

    fun setItemLongClickListener(longClickListener: ItemLongClickListener) {
        this.itemLongClickListener = longClickListener
    }

    fun setItemLongClickReleaseListener(longClickReleaseListener: ItemLongClickReleaseListener) {
        onItemLongClickRelease = longClickReleaseListener
    }
    // endregion

    // region 数据操作
    fun insert(elem: T) {
        insert(data.size, elem)
    }

    fun insert(_position: Int, elem: T) {
        var position = _position
        if (position > data.size) position = data.size
        data.add(position, elem)
        notifyItemInserted(position)
        if (data.size > position + 1) { //不刷新，索引下标会出现错误
            notifyItemRangeChanged(position + 1, data.size - position - 1)
        }
    }

    /**
     * add进行添加数据，并刷新所有数据
     * 后面全部使用insertAll，不进行刷新插入位置之前的的数据
     */
    //insert进行插入，并刷新插入位置之后的数据
    fun insertAll(elem: List<T>) {
        insertAll(data.size, elem)
    }

    fun insertAll(_position: Int, elem: List<T>) {
        var position = _position
        if (elem.isNotEmpty()) {
            if (position > data.size) position = data.size
            data.addAll(position, elem)
            notifyItemRangeInserted(position, elem.size)
            if (data.size > position + elem.size) { //不刷新，索引下标会出现错误
                notifyItemRangeChanged(position + elem.size, data.size - position - elem.size)
            }
        }
    }

    fun set(oldElem: T, newElem: T) {
        set(data.indexOf(oldElem), newElem)
    }

    fun set(index: Int, elem: T) {
        data[index] = elem
        notifyItemChanged(index)
    }

    fun remove(elem: T) {
        if (elem != null) {
            val index: Int = data.indexOf(elem)
            data.remove(elem)
            notifyItemRemoved(index)
            if (data.size - 1 >= index) notifyItemRangeChanged(index, data.size - index)
        }
    }

    fun remove(index: Int) {
        if (index < data.size) {
            data.removeAt(index)
            notifyItemRemoved(index)
            if (data.size - 1 >= index) notifyItemRangeChanged(index, data.size - index)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun remove(elem: List<T>) {
        if (elem.isNotEmpty()) {
            data.removeAll(elem)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeRange(index: Int, count: Int) {
        var realCount = 0
        for (i in index + count - 1 downTo index) {
            if (i >= data.size) continue
            data.removeAt(i)
            realCount++
        }
        notifyItemRangeRemoved(index, realCount)
        if (data.size - 1 >= index) {
            if (index == 0) notifyDataSetChanged()
            else notifyItemRangeChanged(
                index,
                data.size - index
            )
        }
    }

    fun removeAfter(index: Int) {
        if (index >= data.size) return
        removeRange(index, data.size - index)
    }

    fun getItem(_position: Int): T {
        var position = _position
        if (position >= data.size) position = data.size - 1
        return data[position]
    }

    fun getAll(): List<T> {
        return data
    }

    fun contains(elem: T): Boolean {
        return data.contains(elem)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }
    // endregion
}