package com.shrimp.base.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity

/**
 * Created by chasing on 2021/11/3.
 */
class DrawableCenterTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(
        context: Context, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context) : super(context)

    override fun onDraw(canvas: Canvas) {
        val drawables = compoundDrawables
        val drawableLeft = drawables[0]
        val drawableRight = drawables[2]
        val textWidth = paint.measureText(text.toString())
        if (null != drawableLeft) {
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            val contentWidth = textWidth + compoundDrawablePadding + drawableLeft.intrinsicWidth
            if (width - contentWidth > 0) {
                canvas.translate((width - contentWidth - paddingRight - paddingLeft) / 2, 0f)
            }
        }
        if (null != drawableRight) {
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            val contentWidth = textWidth + compoundDrawablePadding + drawableRight.intrinsicWidth
            if (width - contentWidth > 0) {
                canvas.translate(-(width - contentWidth - paddingRight - paddingLeft) / 2, 0f)
            }
        }
        super.onDraw(canvas)
    }
}