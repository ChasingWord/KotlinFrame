package com.shrimp.base.utils.decoration

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shrimp.base.R
import java.util.*
import kotlin.math.abs

/**
 * Created by chasing on 2021/10/22.
 */
class DividerGridItemDecoration (var context: Context): RecyclerView.ItemDecoration() {

    private var mDivider: Drawable? = null
    private var mPaint: Paint? = null
    private var mColorResId = 0
    private var preHorizontalBottom = 0
    private var mHorizontalLineWidth = 0
    private var mVerticalLineWidth = 0

    fun colorResId(@ColorRes resId: Int): DividerGridItemDecoration {
        mColorResId = resId
        return this
    }

    //设置水平分割线的宽度
    fun widthResId(@DimenRes resId: Int): DividerGridItemDecoration {
        mHorizontalLineWidth = context.resources.getDimensionPixelSize(resId)
        return this
    }

    //设置垂直分割线的宽度
    fun widthOfVerticalResId(@DimenRes resId: Int): DividerGridItemDecoration {
        mVerticalLineWidth = context.resources.getDimensionPixelSize(resId)
        return this
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        // 列数
        var spanCount = -1
        val layoutManager: RecyclerView.LayoutManager? = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount: Int = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            // 最后一条不画
            if (parent.getChildLayoutPosition(child) == (parent.layoutManager?.itemCount ?: 0) - 1) {
                continue
            }
            if (mHorizontalLineWidth > 0) {
                if (mColorResId > 0) {
                    mPaint!!.color = ContextCompat.getColor(context, mColorResId)
                    mPaint!!.strokeWidth = mHorizontalLineWidth.toFloat()
                    if (preHorizontalBottom - child.bottom != 0 && abs(preHorizontalBottom - child.bottom) < 10) {
                        // 一行的最后一个的bottom莫名小了3px
                        c.drawLine(
                            (child.left - mHorizontalLineWidth).toFloat(),
                            preHorizontalBottom + mHorizontalLineWidth / 2f,
                            (child.right + mHorizontalLineWidth).toFloat(),
                            preHorizontalBottom + mHorizontalLineWidth / 2f,
                            mPaint!!
                        )
                    } else {
                        preHorizontalBottom = child.bottom
                        c.drawLine(
                            (child.left - mHorizontalLineWidth).toFloat(),
                            child.bottom + mHorizontalLineWidth / 2f,
                            (child.right + mHorizontalLineWidth).toFloat(),
                            child.bottom + mHorizontalLineWidth / 2f,
                            mPaint!!
                        )
                    }
                } else {
                    val params: RecyclerView.LayoutParams = child
                        .layoutParams as RecyclerView.LayoutParams
                    val left: Int = child.left - params.leftMargin
                    val right: Int = (child.right + params.rightMargin
                            + mDivider!!.intrinsicWidth)
                    val top: Int = child.bottom + params.bottomMargin
                    val bottom = top + mDivider!!.intrinsicHeight
                    mDivider!!.setBounds(left, top, right, bottom + mHorizontalLineWidth)
                    mDivider!!.draw(c)
                }
            }
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount: Int = parent.childCount
        val span = getSpanCount(parent)
        for (i in 0 until childCount) {
            if (i % span == span - 1) continue
            val child: View = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child
                .layoutParams as RecyclerView.LayoutParams
            val top: Int = child.top - params.topMargin
            val bottom: Int = child.bottom + params.bottomMargin
            val left: Int = child.right + params.rightMargin
            val right = left + mDivider!!.intrinsicWidth
            if (mVerticalLineWidth > 0) {
                if (mColorResId > 0) {
                    mPaint!!.color = ContextCompat.getColor(context, mColorResId)
                    mPaint!!.strokeWidth = mVerticalLineWidth.toFloat()
                    c.drawLine(
                        child.right + mVerticalLineWidth / 2.toFloat(),
                        (child.top - mVerticalLineWidth).toFloat(),
                        child.right + mVerticalLineWidth / 2.toFloat(),
                        (child.bottom + mVerticalLineWidth).toFloat(),
                        mPaint!!
                    )
                } else {
                    mDivider!!.setBounds(left, top, right + mVerticalLineWidth, bottom)
                    mDivider!!.draw(c)
                }
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val span = getSpanCount(parent)
        val padding = mVerticalLineWidth / span
        val itemPosition: Int =
            (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        val columnIndex = itemPosition % span
        val rowIndex = itemPosition / span
        outRect.left = columnIndex * padding
        outRect.right = mVerticalLineWidth - outRect.left - padding
        if (rowIndex != 0) outRect.top = mHorizontalLineWidth.coerceAtLeast(0) else outRect.top = 0
        outRect.bottom = 0
    }
}
