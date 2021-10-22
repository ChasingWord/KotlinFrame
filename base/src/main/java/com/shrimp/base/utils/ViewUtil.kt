package com.shrimp.base.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.View
import android.widget.EditText
import android.widget.TextView

/**
 * Created by chasing on 2021/10/22.
 */
object ViewUtil {
    /**
     * EditText竖直方向是否可以滚动
     * (文本高度超过控件高度或者文本长度超过控件宽度)
     *
     * @param editText 需要判断的EditText
     * @return true：可以滚动   false：不可以滚动
     */
    fun canVerticalScroll(editText: EditText): Boolean {
        //滚动的距离
        val scrollY = editText.scrollY
        //控件内容的总高度
        val scrollRange = editText.layout.height
        //控件实际显示的高度
        val scrollExtent =
            editText.height - editText.compoundPaddingTop - editText.compoundPaddingBottom
        //控件内容总高度与实际显示高度的差值
        val scrollDifference = scrollRange - scrollExtent
        return scrollDifference != 0 && (scrollY > 0 || scrollY < scrollDifference - 1)
    }

    fun getViewWidth(view: View): Int {
        var width = view.width
        if (width == 0) {
            val size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(size, size)
            width = view.measuredWidth
        }
        return width
    }

    fun getViewHeight(view: View): Int {
        var height = view.height
        if (height == 0) {
            val size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(size, size)
            height = view.measuredHeight
        }
        return height
    }

    fun getViewSize(view: View): IntArray {
        val sizeArr = intArrayOf(view.width, view.height)
        if (sizeArr[0] == 0 || sizeArr[1] == 0) {
            val size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(size, size)
            if (sizeArr[0] == 0) sizeArr[0] = view.measuredWidth
            if (sizeArr[1] == 0) sizeArr[1] = view.measuredHeight
        }
        return sizeArr
    }

    fun setGradientTextColor(textView: TextView, startColor: Int, endColor: Int) {
        val mLinearGradient: LinearGradient = LinearGradient(
            0f, 0f, textView.paint.textSize * textView.text.length.toFloat(), 0f,
            startColor, endColor, Shader.TileMode.CLAMP
        )
        textView.paint.shader = mLinearGradient
        textView.invalidate()
    }
}