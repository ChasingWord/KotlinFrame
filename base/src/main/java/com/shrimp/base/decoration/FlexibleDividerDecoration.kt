package com.shrimp.base.decoration

import android.R
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shrimp.base.decoration.FlexibleDividerDecoration.*

/**
 * Created by chasing on 2021/10/22.
 */
abstract class FlexibleDividerDecoration(builder: Builder) : RecyclerView.ItemDecoration() {

    private val defaultSize = 2
    private val attrs = intArrayOf(
        R.attr.listDivider
    )

    protected enum class DividerType {
        DRAWABLE, PAINT, COLOR
    }

    protected var dividerType: DividerType? = null
    protected var visibilityProvider: VisibilityProvider? = null
    protected var paintProvider: PaintProvider? = null
    protected var colorProvider: ColorProvider? = null
    protected var drawableProvider: DrawableProvider? = null
    protected var sizeProvider //item间分隔线大小
            : SizeProvider? = null
    protected var showLastDivider //是否显示最后一个item的分割线
            = false
    protected var positionInsideItem = false
    private var paint: Paint? = null

    private var topPaint: Paint? = null
    protected var topSizeProvider //第一个item向上/向左的分割线
            : SizeProvider? = null
    protected var showTopDivider //是否展示第一个item向上/向左的分割线
            = false

    init {
        when {
            builder.paintProvider != null -> {
                dividerType = DividerType.PAINT
                paintProvider = builder.paintProvider
            }
            builder.colorProvider != null -> {
                dividerType = DividerType.COLOR
                colorProvider = builder.colorProvider
                paint = Paint()
                setSizeProvider(builder)
            }
            else -> {
                dividerType = DividerType.DRAWABLE
                drawableProvider = if (builder.drawableProvider == null) {
                    val a = builder.context.obtainStyledAttributes(attrs)
                    val divider = a.getDrawable(0)
                    a.recycle()
                    DrawableProvider { _: Int, _: RecyclerView? -> divider }
                } else {
                    builder.drawableProvider
                }
                sizeProvider = builder.sizeProvider
                topSizeProvider = builder.topSizeProvider
            }
        }
        visibilityProvider = builder.visibilityProvider
        showLastDivider = builder.showLastDivider
        showTopDivider = builder.showTopDivider
        positionInsideItem = builder.positionInsideItem
        if (showTopDivider) {
            topPaint = Paint()
        }
    }

    private fun setSizeProvider(builder: Builder) {
        sizeProvider = builder.sizeProvider
        topSizeProvider = builder.topSizeProvider
        if (sizeProvider == null) {
            sizeProvider = SizeProvider { _: Int, _: RecyclerView? -> defaultSize }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter ?: return
        val itemCount: Int = adapter.itemCount
        val lastDividerOffset = getLastDividerOffset(parent)
        val validChildCount: Int = parent.childCount
        var lastChildPosition = -1
        for (i in 0 until validChildCount) {
            val child: View = parent.getChildAt(i)
            val childPosition: Int = parent.getChildAdapterPosition(child)
            if (childPosition < lastChildPosition) {
                // Avoid remaining divider when animation starts
                continue
            }
            lastChildPosition = childPosition
            if (!showLastDivider && childPosition >= itemCount - lastDividerOffset) {
                // Don't draw divider for last line if mShowLastDivider = false
                continue
            }
            if (wasDividerAlreadyDrawn(childPosition, parent)) {
                // No need to draw divider again as it was drawn already by previous column
                continue
            }
            val groupIndex = getGroupIndex(childPosition, parent)
            //如果视图处于刷新状态，会返回NO_POSITION即-1
            if (groupIndex < 0 || visibilityProvider!!.shouldHideDivider(groupIndex, parent)) {
                continue
            }
            val bounds = getDividerBound(groupIndex, parent, child)
            when (dividerType) {
                DividerType.DRAWABLE -> {
                    val drawable = drawableProvider!!.drawableProvider(groupIndex, parent)
                    if (drawable != null) {
                        drawable.bounds = bounds
                        drawable.draw(c)
                    }
                }
                DividerType.PAINT -> {
                    paint = paintProvider!!.dividerPaint(groupIndex, parent)
                    c.drawLine(
                        bounds.left.toFloat(),
                        bounds.top.toFloat(),
                        bounds.right.toFloat(),
                        bounds.bottom.toFloat(),
                        paint!!
                    )
                }
                DividerType.COLOR -> {
                    paint!!.color = colorProvider!!.dividerColor(groupIndex, parent)
                    paint!!.strokeWidth = sizeProvider!!.dividerSize(groupIndex, parent).toFloat()
                    if (childPosition == 0 && showTopDivider) {
                        topPaint!!.color = colorProvider!!.dividerColor(groupIndex, parent)
                        if (topSizeProvider != null) topPaint!!.strokeWidth =
                            topSizeProvider!!.dividerSize(groupIndex, parent)
                                .toFloat() else topPaint!!.strokeWidth =
                            sizeProvider!!.dividerSize(groupIndex, parent).toFloat()
                        c.drawLine(
                            bounds.left.toFloat(),
                            bounds.top - child.height - topPaint!!.strokeWidth / 2,
                            bounds.right.toFloat(),
                            bounds.bottom - child.height - topPaint!!.strokeWidth / 2,
                            topPaint!!
                        )
                    }
                    c.drawLine(
                        bounds.left.toFloat(),
                        bounds.top.toFloat(),
                        bounds.right.toFloat(),
                        bounds.bottom.toFloat(),
                        paint!!
                    )
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
        val position: Int = parent.getChildAdapterPosition(view)

//        int itemCount = parent.getAdapter().getItemCount();
//        int lastDividerOffset = getLastDividerOffset(parent);
//        if (!mShowLastDivider && position >= itemCount - lastDividerOffset) {
//            // bug:如果是一个一个插入刷新的话，会把每一个都当做是最后一个，最后导致都没有间距
//            // Don't set item offset for last line if mShowLastDivider = false
//            return;
//        }

        //如果视图处于刷新状态，会返回NO_POSITION即-1
        val groupIndex = getGroupIndex(position, parent)
        if (groupIndex < 0 || visibilityProvider!!.shouldHideDivider(groupIndex, parent)) {
            return
        }
        setItemOffsets(outRect, groupIndex, parent)
    }

    /**
     * Check if recyclerview is reverse layout
     *
     * @param parent RecyclerView
     * @return true if recyclerview is reverse layout
     */
    protected fun isReverseLayout(parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        return if (layoutManager is LinearLayoutManager) {
            layoutManager.reverseLayout
        } else {
            false
        }
    }

    /**
     * In the case mShowLastDivider = false,
     * Returns offset for how many views we don't have to draw a divider for,
     * for LinearLayoutManager it is as simple as not drawing the last child divider,
     * but for a GridLayoutManager it needs to take the span count for the last items into account
     * until we use the span count configured for the grid.
     *
     * @param parent RecyclerView
     * @return offset for how many views we don't have to draw a divider or 1 if its a
     * LinearLayoutManager
     */
    private fun getLastDividerOffset(parent: RecyclerView): Int {
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager = parent.layoutManager as GridLayoutManager
            val spanSizeLookup = layoutManager.spanSizeLookup
            val spanCount: Int = layoutManager.spanCount
            val itemCount: Int = parent.adapter?.itemCount ?: 0
            for (i in itemCount - 1 downTo 0) {
                if (spanSizeLookup.getSpanIndex(i, spanCount) == 0) {
                    return itemCount - i
                }
            }
        }
        return 1
    }

    /**
     * Determines whether divider was already drawn for the row the item is in,
     * effectively only makes sense for a grid
     *
     * @param position current view position to draw divider
     * @param parent   RecyclerView
     * @return true if the divider can be skipped as it is in the same row as the previous one.
     */
    private fun wasDividerAlreadyDrawn(position: Int, parent: RecyclerView): Boolean {
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager = parent.layoutManager as GridLayoutManager
            val spanSizeLookup = layoutManager.spanSizeLookup
            val spanCount: Int = layoutManager.spanCount
            return spanSizeLookup.getSpanIndex(position, spanCount) > 0
        }
        return false
    }

    /**
     * Returns a group index for GridLayoutManager.
     * for LinearLayoutManager, always returns position.
     *
     * @param position current view position to draw divider
     * @param parent   RecyclerView
     * @return group index of items
     */
    private fun getGroupIndex(position: Int, parent: RecyclerView): Int {
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
            val spanSizeLookup = layoutManager.spanSizeLookup
            val spanCount: Int = layoutManager.spanCount
            return spanSizeLookup.getSpanGroupIndex(position, spanCount)
        }
        return position
    }

    protected abstract fun getDividerBound(position: Int, parent: RecyclerView?, child: View?): Rect

    protected abstract fun setItemOffsets(outRect: Rect?, position: Int, parent: RecyclerView?)

    /**
     * Interface for controlling divider visibility
     */
    fun interface VisibilityProvider {
        /**
         * Returns true if divider should be hidden.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return True if the divider at position should be hidden
         */
        fun shouldHideDivider(position: Int, parent: RecyclerView?): Boolean
    }

    /**
     * Interface for controlling paint instance for divider drawing
     */
    fun interface PaintProvider {
        /**
         * Returns [Paint] for divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Paint instance
         */
        fun dividerPaint(position: Int, parent: RecyclerView?): Paint?
    }

    /**
     * Interface for controlling divider color
     */
    fun interface ColorProvider {
        /**
         * Returns [android.graphics.Color] value of divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Color value
         */
        fun dividerColor(position: Int, parent: RecyclerView?): Int
    }

    /**
     * Interface for controlling drawable object for divider drawing
     */
    fun interface DrawableProvider {
        /**
         * Returns drawable instance for divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Drawable instance
         */
        fun drawableProvider(position: Int, parent: RecyclerView?): Drawable?
    }

    /**
     * Interface for controlling divider size
     */
    fun interface SizeProvider {
        /**
         * Returns size value of divider.
         * Height for horizontal divider, width for vertical divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Size of divider
         */
        fun dividerSize(position: Int, parent: RecyclerView?): Int
    }

    open class Builder(val context: Context) {
        protected var resources: Resources
        var paintProvider: PaintProvider? = null
        var colorProvider: ColorProvider? = null
        var drawableProvider: DrawableProvider? = null
        var sizeProvider: SizeProvider? = null
        var visibilityProvider: VisibilityProvider =
            VisibilityProvider { position: Int, parent: RecyclerView? -> false }
        var showLastDivider = false
        var topSizeProvider: SizeProvider? = null
        var showTopDivider = false
        var positionInsideItem = false
        fun paint(paint: Paint?): Builder {
            return paintProvider { _: Int, _: RecyclerView? -> paint }
        }

        fun paintProvider(provider: PaintProvider): Builder {
            paintProvider = provider
            return this
        }

        fun color(color: Int): Builder {
            return colorProvider { _: Int, _: RecyclerView? -> color }
        }

        fun colorResId(@ColorRes colorId: Int): Builder {
            return color(ContextCompat.getColor(context, colorId))
        }

        fun colorProvider(provider: ColorProvider?): Builder {
            colorProvider = provider
            return this
        }

        fun drawable(@DrawableRes id: Int): Builder {
            return drawable(ContextCompat.getDrawable(context, id))
        }

        fun drawable(drawable: Drawable?): Builder {
            return drawableProvider { _: Int, _: RecyclerView? -> drawable }
        }

        fun drawableProvider(provider: DrawableProvider?): Builder {
            drawableProvider = provider
            return this
        }

        fun size(size: Int): Builder {
            return sizeProvider { _: Int, _: RecyclerView? -> size }
        }

        fun sizeResId(@DimenRes sizeId: Int): Builder {
            return size(resources.getDimensionPixelSize(sizeId))
        }

        fun sizeProvider(provider: SizeProvider?): Builder {
            sizeProvider = provider
            return this
        }

        fun topSize(size: Int): Builder {
            return topSizeProvider { position: Int, parent: RecyclerView? -> size }
        }

        fun topSizeResId(@DimenRes sizeId: Int): Builder {
            return topSize(resources.getDimensionPixelSize(sizeId))
        }

        fun topSizeProvider(provider: SizeProvider?): Builder {
            topSizeProvider = provider
            return this
        }

        fun visibilityProvider(provider: VisibilityProvider): Builder {
            visibilityProvider = provider
            return this
        }

        fun showLastDivider(): Builder {
            showLastDivider = true
            return this
        }

        fun showTopDivider(): Builder {
            showTopDivider = true
            return this
        }

        fun positionInsideItem(positionInsideItem: Boolean): Builder {
            this.positionInsideItem = positionInsideItem
            return this
        }

        protected fun checkBuilderParams() {
            if (paintProvider != null) {
                require(colorProvider == null) { "Use setColor method of Paint class to specify line color. Do not provider ColorProvider if you set PaintProvider." }
                require(sizeProvider == null) { "Use setStrokeWidth method of Paint class to specify line size. Do not provider SizeProvider if you set PaintProvider." }
            }
        }

        init {
            resources = context.resources
        }
    }
}