package com.hebao.testkotlin.view.meituan

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivityMeituanBinding
import com.shrimp.base.utils.GenericTools
import com.shrimp.base.utils.ItemTouchCallBack
import com.shrimp.base.utils.StatusBarUtil
import com.shrimp.base.view.BaseActivity
import kotlin.math.abs
import kotlin.math.min

/**
 * Created by chasing on 2022/2/11.
 */
class MeiTuanActivity : BaseActivity<MeiTuanViewModel, ActivityMeituanBinding>() {

    private val colorDrawable = ColorDrawable()

    companion object {
        fun start(context: Context) {
            start(context, MeiTuanActivity::class.java)
        }
    }

    override fun inflateDataBinding(): ActivityMeituanBinding =
        ActivityMeituanBinding.inflate(layoutInflater)

    override fun getViewModelClass(): Class<MeiTuanViewModel> = MeiTuanViewModel::class.java

    override fun changeConfig() {
        needChangeStatusBar = false
        StatusBarUtil.setTransparentStatusBar(this)
    }

    override fun initView() {
        val firstAdapter = FirstRcvAdapter(this)
        val secondAdapter = FirstRcvAdapter(this)
        for (i in 0..100) {
            firstAdapter.insert("String $i")
            secondAdapter.insert("String $i")
        }
        // 空白占位，空出底部购物车的位置
        firstAdapter.insert("")
        secondAdapter.insert("")

        colorDrawable.color = ContextCompat.getColor(this, R.color.white)
        dataBinding.toolbar.post {
            val layoutParams = dataBinding.toolbar.layoutParams as ViewGroup.MarginLayoutParams
            val statusBarHeight = GenericTools.getStatusBarHeight(context)
            layoutParams.height = dataBinding.toolbar.height + statusBarHeight
            dataBinding.toolbar.setPadding(dataBinding.toolbar.paddingStart,
                dataBinding.toolbar.paddingTop + statusBarHeight,
                dataBinding.toolbar.paddingEnd,
                dataBinding.toolbar.paddingBottom)
        }

        dataBinding.firstRcv.layoutManager = LinearLayoutManager(context)
        dataBinding.firstRcv.adapter = firstAdapter

        dataBinding.secondRcv.layoutManager = LinearLayoutManager(context)
        dataBinding.secondRcv.adapter = secondAdapter

        val itemTouchCallback = ItemTouchCallBack()
        itemTouchCallback.isItemViewSwipeEnabled = true
        itemTouchCallback.setOnItemTouchListener(context, firstAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(dataBinding.firstRcv)

        dataBinding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = abs(verticalOffset)
            val alpha = min((offset / appBarLayout.height.toFloat() * 255).toInt(), 255)
            colorDrawable.alpha = alpha
            dataBinding.toolbar.background = colorDrawable
        }
    }

    override fun initDataObserve() {
    }
}