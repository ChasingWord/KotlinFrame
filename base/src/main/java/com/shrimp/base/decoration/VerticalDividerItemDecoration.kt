package com.shrimp.base.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by chasing on 2021/10/22.
 */
class VerticalDividerItemDecoration(builder: Builder) : FlexibleDividerDecoration(builder) {

    private var mMarginProvider: MarginProvider = builder.mMarginProvider

    override fun getDividerBound(position: Int, parent: RecyclerView?, child: View?): Rect {
        val bounds = Rect(0, 0, 0, 0)
        if (parent != null && child != null) {
            val transitionX = child.translationX.toInt()
            val transitionY = child.translationY.toInt()
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            bounds.top = parent.paddingTop +
                    mMarginProvider.dividerTopMargin(position, parent) + transitionY
            bounds.bottom = parent.height - parent.paddingBottom -
                    mMarginProvider.dividerBottomMargin(position, parent) + transitionY
            val dividerSize = getDividerSize(position, parent)
            val isReverseLayout = isReverseLayout(parent)
            if (dividerType === DividerType.DRAWABLE) {
                // set left and right position of divider
                if (isReverseLayout) {
                    bounds.right = child.left - params.leftMargin + transitionX
                    bounds.left = bounds.right - dividerSize
                } else {
                    bounds.left = child.right + params.rightMargin + transitionX
                    bounds.right = bounds.left + dividerSize
                }
            } else {
                // set center point of divider
                val halfSize = dividerSize / 2
                if (isReverseLayout) {
                    bounds.left = child.left - params.leftMargin - halfSize + transitionX
                } else {
                    bounds.left = child.right + params.rightMargin + halfSize + transitionX
                }
                bounds.right = bounds.left
            }
            if (positionInsideItem) {
                if (isReverseLayout) {
                    bounds.left += dividerSize
                    bounds.right += dividerSize
                } else {
                    bounds.left -= dividerSize
                    bounds.right -= dividerSize
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
                        getDividerSize(
                            position,
                            parent
                        ), 0, getTopDividerSize(position, parent), 0
                    ) else
                    outRect?.set(getDividerSize(position, parent), 0, 0, 0)
            } else {
                if (position == 0 && showTopDivider)
                    outRect?.set(
                        getTopDividerSize(
                            position,
                            parent
                        ), 0, getDividerSize(position, parent), 0
                    ) else
                    outRect?.set(0, 0, getDividerSize(position, parent), 0)
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
                drawable!!.intrinsicWidth
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
                return drawable!!.intrinsicWidth
            }
            else -> throw RuntimeException("failed to get size")
        }
    }

    /**
     * Interface for controlling divider margin
     */
    interface MarginProvider {
        /**
         * Returns top margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return top margin
         */
        fun dividerTopMargin(position: Int, parent: RecyclerView?): Int

        /**
         * Returns bottom margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return bottom margin
         */
        fun dividerBottomMargin(position: Int, parent: RecyclerView?): Int
    }

    class Builder(context: Context) : FlexibleDividerDecoration.Builder(context) {
        internal var mMarginProvider: MarginProvider = object : MarginProvider {
            override fun dividerTopMargin(position: Int, parent: RecyclerView?): Int {
                return 0
            }

            override fun dividerBottomMargin(position: Int, parent: RecyclerView?): Int {
                return 0
            }
        }

        private fun marginProvider(provider: MarginProvider): Builder {
            mMarginProvider = provider
            return this
        }

        fun marginResId(@DimenRes topMarginId: Int, @DimenRes bottomMarginId: Int): Builder {
            return marginProvider(object : MarginProvider {
                override fun dividerTopMargin(position: Int, parent: RecyclerView?): Int {
                    return resources.getDimensionPixelSize(topMarginId)
                }

                override fun dividerBottomMargin(position: Int, parent: RecyclerView?): Int {
                    return resources.getDimensionPixelSize(bottomMarginId)
                }
            })
        }

        fun marginResId(@DimenRes verticalMarginId: Int): Builder {
            return marginResId(verticalMarginId, verticalMarginId)
        }

        fun build(): VerticalDividerItemDecoration {
            checkBuilderParams()
            return VerticalDividerItemDecoration(this)
        }
    }
}