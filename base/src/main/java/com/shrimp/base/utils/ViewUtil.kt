package com.shrimp.base.utils

import android.annotation.SuppressLint
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * EditText竖直方向是否可以滚动
 * (文本高度超过控件高度或者文本长度超过控件宽度)
 *
 * @param editText 需要判断的EditText
 * @return true：可以滚动   false：不可以滚动
 */
fun EditText.canVerticalScroll(): Boolean {
    val scrollExtent = height - compoundPaddingTop - compoundPaddingBottom
    //控件内容总高度与实际显示高度的差值
    val scrollDifference = layout.height - scrollExtent
    return scrollDifference != 0 && (scrollY > 0 || scrollY < scrollDifference - 1)
}

fun View.getViewWidth(): Int {
    var actWidth = width
    if (actWidth == 0) {
        val size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        measure(size, size)
        actWidth = measuredWidth
    }
    return actWidth
}

fun View.getViewHeight(): Int {
    var actHeight = height
    if (actHeight == 0) {
        val size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        measure(size, size)
        actHeight = measuredHeight
    }
    return actHeight
}

fun View.getViewSize(): IntArray {
    val sizeArr = intArrayOf(width, height)
    if (sizeArr[0] == 0 || sizeArr[1] == 0) {
        val size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        measure(size, size)
        if (sizeArr[0] == 0) sizeArr[0] = measuredWidth
        if (sizeArr[1] == 0) sizeArr[1] = measuredHeight
    }
    return sizeArr
}

fun TextView.setGradientTextColor(startColor: Int, endColor: Int) {
    val mLinearGradient = LinearGradient(
        0f, 0f, paint.textSize * text.length.toFloat(), 0f,
        startColor, endColor, Shader.TileMode.CLAMP
    )
    paint.shader = mLinearGradient
    invalidate()
}

//设置列表空白出的点击响应
private var scrollWorkX = 0f
private var scrollWorkY = 0f

@SuppressLint("ClickableViewAccessibility")
fun RecyclerView.setOnTouchRecyclerView(onClickListener: View.OnClickListener) {
    setOnTouchListener { v: View, event: MotionEvent ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            scrollWorkX = event.x
            scrollWorkY = event.y
        }
        if (event.action == MotionEvent.ACTION_UP) {
            if (v.id != 0 && abs(scrollWorkX - event.x) <= 5 && abs(scrollWorkY - event.y) <= 5) {
                //recyclerView空白处点击事件
                onClickListener.onClick(v)
            }
        }
        false
    }
}