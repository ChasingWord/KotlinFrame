package com.shrimp.base.utils

import android.app.Service
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.recyclerview.widget.*

/**
 * Created by chasing on 2022/2/12.
 * ItemTouchCallBack touchCallBack = new ItemTouchCallBack();
 * touchCallBack.setOnItemTouchListener(mContext, mPicAdapter);
 * ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallBack);
 * itemTouchHelper.attachToRecyclerView(rcv);
 * 注：
 * 侧滑仅支持侧滑删除，不支持侧滑菜单
 */
class ItemTouchCallBack : ItemTouchHelper.Callback() {
    private var itemViewSwipeEnabled = false
    private var longPressDragEnabled = true
    private lateinit var onItemTouchListener: OnItemTouchListener
    private lateinit var mContext: Context

    fun setOnItemTouchListener(context: Context, onItemTouchListener: OnItemTouchListener) {
        mContext = context
        this.onItemTouchListener = onItemTouchListener
    }

    /**
     * 根据 RecyclerView 不同的布局管理器，设置不同的滑动、拖动方向
     * 该方法使用 makeMovementFlags(int dragFlags, int swipeFlags) 方法返回
     * 参数: dragFlags:拖动的方向
     * swipeFlags:滑动的方向
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (recyclerView.layoutManager is GridLayoutManager ||
            recyclerView.layoutManager is StaggeredGridLayoutManager
        ) {
            //此处不需要进行滑动操作，可设置为除4和8之外的整数，这里设为0
            //不支持滑动
            return makeMovementFlags(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0
            )
        } else {
            var orientationVertical = true
            if (recyclerView.layoutManager is LinearLayoutManager)
                orientationVertical =
                    (recyclerView.layoutManager as LinearLayoutManager).orientation == RecyclerView.VERTICAL
            //如果是LinearLayoutManager则只能向上向下滑动，
            //此处第二个参数设置支持向右滑动ItemTouchHelper.RIGHT
            return makeMovementFlags(
                if (orientationVertical) ItemTouchHelper.UP or ItemTouchHelper.DOWN else ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                if (!orientationVertical) ItemTouchHelper.UP else ItemTouchHelper.LEFT
            )
        }
    }

    /**
     * 当 ItemTouchHelper 拖动一个Item时该方法将会被回调，Item将从旧的位置移动到新的位置
     * 如果不拖动这个方法将从来不会调用,返回true表示已经被移动到新的位置
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        onItemTouchListener.onMove(fromPosition, toPosition)
        return true
    }

    /**
     * 当Item被滑动的时候被调用
     * 如果你不滑动这个方法将不会被调用
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //此处是侧滑删除的主要代码
        val position = viewHolder.bindingAdapterPosition
        onItemTouchListener.onSwiped(position)
    }

    /**
     * 当Item被滑动、拖动的时候被调用
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            //获取系统震动服务
            val vib: Vibrator = mContext.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vib.vibrate(70)
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * 当与用户交互结束或相关动画完成之后被调用
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return itemViewSwipeEnabled
    }

    fun setItemViewSwipeEnabled(itemViewSwipeEnabled: Boolean) {
        this.itemViewSwipeEnabled = itemViewSwipeEnabled
    }

    override fun isLongPressDragEnabled(): Boolean {
        return longPressDragEnabled
    }

    fun setLongPressDragEnabled(longPressDragEnabled: Boolean) {
        this.longPressDragEnabled = longPressDragEnabled
    }

    /**
     * 移动交换数据的更新监听
     */
    interface OnItemTouchListener {
        //拖动Item时调用，需要执行以下代码，进行交换item对应的数据位置，然后调用notifyItemMoved才会进行位置的切换
        /*
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
         */
        fun onMove(fromPosition: Int, toPosition: Int)

        //滑动Item时调用
        fun onSwiped(position: Int)
    }
}