package com.shrimp.base.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by chasing on 2021/10/22.
 */
class SpannedGridItemDecoration(private val decorationSize: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = decorationSize / 2
        outRect.right = decorationSize / 2
        outRect.top = decorationSize / 2
        outRect.bottom = decorationSize / 2
    }
}
