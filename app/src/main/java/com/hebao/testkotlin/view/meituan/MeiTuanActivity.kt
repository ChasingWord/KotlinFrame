package com.hebao.testkotlin.view.meituan

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hebao.testkotlin.databinding.ActivityMeituanBinding
import com.shrimp.base.utils.GenericTools
import com.shrimp.base.utils.ItemTouchCallBack
import com.shrimp.base.utils.StatusBarUtil
import com.shrimp.base.view.BaseActivity

/**
 * Created by chasing on 2022/2/11.
 */
class MeiTuanActivity : BaseActivity<MeiTuanViewModel, ActivityMeituanBinding>() {

    companion object{
        fun start(context: Context){
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

        val layoutParams = dataBinding.toolbar.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = GenericTools.getStatusBarHeight(context)

        dataBinding.firstRcv.layoutManager = LinearLayoutManager(context)
        dataBinding.firstRcv.adapter = firstAdapter

        dataBinding.secondRcv.layoutManager = LinearLayoutManager(context)
        dataBinding.secondRcv.adapter = secondAdapter

        val itemTouchCallback = ItemTouchCallBack()
        itemTouchCallback.setOnItemTouchListener(context, firstAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(dataBinding.firstRcv)
    }

    override fun initDataObserve() {
    }
}