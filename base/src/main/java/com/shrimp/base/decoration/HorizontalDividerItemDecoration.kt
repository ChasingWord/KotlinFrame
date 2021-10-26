package com.shrimp.base.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by chasing on 2021/10/22.
 */
class HorizontalDividerItemDecoration(builder: Builder) : FlexibleDividerDecoration(builder) {

    private var mMarginProvider: MarginProvider = builder.mMarginProvider

    override fun getDividerBound(position: Int, parent: RecyclerView?, child: View?): Rect {
        val bounds = Rect(0, 0, 0, 0)
        if (parent != null && child != null) {
            val transitionX = child.translationX.toInt()
            val transitionY = child.translationY.toInt()
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            bounds.left = parent.paddingLeft +
                    mMarginProvider.dividerLeftMargin(position, parent) + transitionX
            bounds.right = parent.width - parent.paddingRight -
                    mMarginProvider.dividerRightMargin(position, parent) + transitionX
            val dividerSize = getDividerSize(position, parent)
            val isReverseLayout = isReverseLayout(parent)
            if (dividerType === DividerType.DRAWABLE) {
                // set top and bottom position of divider
                if (isReverseLayout) {
                    bounds.bottom = child.top - params.topMargin + transitionY
                    bounds.top = bounds.bottom - dividerSize
                } else {
                    bounds.top = child.bottom + params.bottomMargin + transitionY
                    bounds.bottom = bounds.top + dividerSize
                }
            } else {
                // set center point of divider
                val halfSize = dividerSize / 2
                if (isReverseLayout) {
                    bounds.top = child.top - params.topMargin - halfSize + transitionY
                } else {
                    bounds.top = child.bottom + params.bottomMargin + halfSize + transitionY
                }
                bounds.bottom = bounds.top
            }
            if (positionInsideItem) {
                if (isReverseLayout) {
                    bounds.top += dividerSize
                    bounds.bottom += dividerSize
                } else {
                    bounds.top -= dividerSize
                    bounds.bottom -= dividerSize
                }
            }
        }
        return bounds
    }

    override fun setItemOffsets(outRect: Rect?, position: Int, parent: RecyclerView?) {
        if (positionInsideItem) {
            outRect?.set(0, 0, 0, 0)
            return
        }
        parent?.let {
            if (isReverseLayout(parent)) {
                if (position == 0 && showTopDivider)
                    outRect?.set(
                        0, getDividerSize(position, parent), 0,
                        getTopDividerSize(position, parent)
                    ) else
                    outRect?.set(
                        0, getDividerSize(
                            position,
                            parent
                        ), 0, 0
                    )
            } else {
                if (position == 0 && showTopDivider)
                    outRect?.set(
                        0, getTopDividerSize(
                            position,
                            parent
                        ), 0, getDividerSize(position, parent)
                    ) else
                    outRect?.set(0, 0, 0, getDividerSize(position, parent))
            }
        }
    }

    private fun getDividerSize(position: Int, parent: RecyclerView): Int {
        return when {
            paintProvider != null -> {
                paintProvider!!.dividerPaint(position, parent)!!.strokeWidth.toInt()
            }
            sizeProvider != null -> {
                sizeProvider!!.dividerSize(position, parent)
            }
            drawableProvider != null -> {
                val drawable = drawableProvider!!.drawableProvider(position, parent)
                drawable!!.intrinsicHeight
            }
            else -> throw RuntimeException("failed to get size")
        }
    }

    private fun getTopDividerSize(position: Int, parent: RecyclerView): Int {
        when {
            paintProvider != null -> {
                return paintProvider!!.dividerPaint(position, parent)!!.strokeWidth.toInt()
            }
            topSizeProvider != null -> {
                return topSizeProvider!!.dividerSize(position, parent)
            }
            sizeProvider != null -> {
                return sizeProvider!!.dividerSize(position, parent)
            }
            drawableProvider != null -> {
                val drawable = drawableProvider!!.drawableProvider(position, parent)
                return drawable!!.intrinsicHeight
            }
            else -> throw RuntimeException("failed to get size")
        }
    }

    /**
     * Interface for controlling divider margin
     */
    interface MarginProvider {
        /**
         * Returns left margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return left margin
         */
        fun dividerLeftMargin(position: Int, parent: RecyclerView?): Int

        /**
         * Returns right margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return right margin
         */
        fun dividerRightMargin(position: Int, parent: RecyclerView?): Int
    }

    class Builder(context: Context) : FlexibleDividerDecoration.Builder(context) {
        internal var mMarginProvider: MarginProvider = object : MarginProvider {
            override fun dividerLeftMargin(position: Int, parent: RecyclerView?): Int {
                return 0
            }

            override fun dividerRightMargin(position: Int, parent: RecyclerView?): Int {
                return 0
            }
        }

        private fun marginProvider(provider: MarginProvider): Builder {
            mMarginProvider = provider
            return this
        }

        fun marginResId(@DimenRes leftMarginId: Int, @DimenRes rightMarginId: Int): Builder {
            return marginProvider(object : MarginProvider {
                override fun dividerLeftMargin(position: Int, parent: RecyclerView?): Int {
                    return resources.getDimensionPixelSize(leftMarginId)
                }

                override fun dividerRightMargin(position: Int, parent: RecyclerView?): Int {
                    return resources.getDimensionPixelSize(rightMarginId)
                }
            })
        }

        fun marginResId(@DimenRes horizontalMarginId: Int): Builder {
            return marginResId(horizontalMarginId, horizontalMarginId)
        }

        override fun build(): FlexibleDividerDecoration {
            return HorizontalDividerItemDecoration(this)
        }
    }
}