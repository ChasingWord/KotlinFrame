package com.shrimp.base.widgets.refresh

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.shrimp.base.R
import kotlin.math.abs

/**
 * Created by chasing on 2021/11/3.
 */
class SmartRefreshFooter(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attributeSet, defStyleAttr), RefreshFooter {

    private var frameAnim: AnimationDrawable? = null
    private var isRefreshing = false
    private var prePercent = 0f

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context) : this(context, null)

    init {
        val circleView = ImageView(context)

        frameAnim = ContextCompat.getDrawable(context, R.drawable.refresh_anim) as AnimationDrawable
        circleView.background = frameAnim

        addView(circleView)
        gravity = Gravity.CENTER
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
    }

    override fun getView(): View = this

    override fun getSpinnerStyle(): SpinnerStyle = SpinnerStyle.Translate

    override fun setPrimaryColors(vararg colors: Int) {
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        if (!isRefreshing) {
            if (abs(prePercent - percent) > 0.009) start() else stop()
        }
        prePercent = percent
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        isRefreshing = true
        start()
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        isRefreshing = false
        stop()
        return 0 //延迟xx毫秒之后再弹回
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean = false

    override fun setNoMoreData(noMoreData: Boolean): Boolean = true

    /**
     * 开始播放
     */
    private fun start() {
        if (frameAnim != null && !frameAnim!!.isRunning) {
            frameAnim!!.start()
        }
    }

    /**
     * 停止播放
     */
    private fun stop() {
        if (frameAnim != null && frameAnim!!.isRunning) {
            frameAnim!!.stop()
        }
    }
}