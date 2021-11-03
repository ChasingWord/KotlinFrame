package com.shrimp.base.widgets.tab

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.shrimp.base.R


/**
 * Created by chasing on 2021/11/3.
 */
class MsgView : AppCompatTextView {
    private val gdBackground = GradientDrawable()
    private var backgroundColor = 0
    private var cornerRadius = 0
    private var strokeWidth = 0
    private var strokeColor = 0
    private var isRadiusHalfHeight = false
    private var isWidthHeightEqual = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        obtainAttributes(context, attrs)
    }

    private fun obtainAttributes(context: Context, attrs: AttributeSet?) {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MsgView)
        backgroundColor = ta.getColor(R.styleable.MsgView_mv_backgroundColor, Color.TRANSPARENT)
        cornerRadius = ta.getDimensionPixelSize(R.styleable.MsgView_mv_cornerRadius, 0)
        strokeWidth = ta.getDimensionPixelSize(R.styleable.MsgView_mv_strokeWidth, 0)
        strokeColor = ta.getColor(R.styleable.MsgView_mv_strokeColor, Color.TRANSPARENT)
        isRadiusHalfHeight = ta.getBoolean(R.styleable.MsgView_mv_isRadiusHalfHeight, false)
        isWidthHeightEqual = ta.getBoolean(R.styleable.MsgView_mv_isWidthHeightEqual, false)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isWidthHeightEqual() && width > 0 && height > 0) {
            val max = width.coerceAtLeast(height)
            val measureSpec = MeasureSpec.makeMeasureSpec(max, MeasureSpec.EXACTLY)
            super.onMeasure(measureSpec, measureSpec)
            return
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isRadiusHalfHeight()) {
            setCornerRadius(height / 2)
        } else {
            setBgSelector()
        }
    }


    override fun setBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
        setBgSelector()
    }

    fun setCornerRadius(cornerRadius: Int) {
        this.cornerRadius = dp2px(cornerRadius.toFloat())
        setBgSelector()
    }

    fun setStrokeWidth(strokeWidth: Int) {
        this.strokeWidth = dp2px(strokeWidth.toFloat())
        setBgSelector()
    }

    fun setStrokeColor(strokeColor: Int) {
        this.strokeColor = strokeColor
        setBgSelector()
    }

    fun setIsRadiusHalfHeight(isRadiusHalfHeight: Boolean) {
        this.isRadiusHalfHeight = isRadiusHalfHeight
        setBgSelector()
    }

    fun setIsWidthHeightEqual(isWidthHeightEqual: Boolean) {
        this.isWidthHeightEqual = isWidthHeightEqual
        setBgSelector()
    }

    fun getBackgroundColor(): Int {
        return backgroundColor
    }

    fun getCornerRadius(): Int {
        return cornerRadius
    }

    fun getStrokeWidth(): Int {
        return strokeWidth
    }

    fun getStrokeColor(): Int {
        return strokeColor
    }

    fun isRadiusHalfHeight(): Boolean {
        return isRadiusHalfHeight
    }

    fun isWidthHeightEqual(): Boolean {
        return isWidthHeightEqual
    }

    protected fun dp2px(dp: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    protected fun sp2px(sp: Float): Int {
        val scale: Float = context.resources.displayMetrics.scaledDensity
        return (sp * scale + 0.5f).toInt()
    }

    private fun setDrawable(gd: GradientDrawable, color: Int, strokeColor: Int) {
        gd.setColor(color)
        gd.cornerRadius = cornerRadius.toFloat()
        gd.setStroke(strokeWidth, strokeColor)
    }

    fun setBgSelector() {
        background = StateListDrawable()
        setDrawable(gdBackground, backgroundColor, strokeColor)
    }
}