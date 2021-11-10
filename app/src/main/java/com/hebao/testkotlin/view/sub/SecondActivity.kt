package com.hebao.testkotlin.view.sub

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivitySecondBinding
import com.shrimp.base.decoration.HorizontalDividerItemDecoration
import com.shrimp.base.view.BaseActivity

/**
 * Created by chasing on 2021/10/25.
 */
class SecondActivity : BaseActivity<SecondViewModel, ActivitySecondBinding>() {

    companion object {
        fun start(context: Context) {
            start(context, SecondActivity::class.java)
        }
    }

    override fun changeConfig() {
        needChangeStatusBar = false
    }

    override fun initViewModel(): Class<SecondViewModel> = SecondViewModel::class.java

    override fun initContentView() = ActivitySecondBinding.inflate(layoutInflater)

    override fun initView() {
        dataBinding.vm = baseViewModel

        dataBinding.rcv.layoutManager = LinearLayoutManager(context)
        dataBinding.rcv.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(context).marginResId(R.dimen.fab_margin)
                .colorResId(
                    R.color.teal_700
                ).build()
        )
        val secondAdapter = SecondAdapter(context)
        dataBinding.rcv.adapter = secondAdapter

        baseViewModel.data.observe(this, {
            secondAdapter.insertAll(it)
        })

        dataBinding.rcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is LinearLayoutManager) {
                    val findLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    if (findLastVisibleItemPosition + 1 == recyclerView.adapter?.itemCount) {
                        baseViewModel.refresh(10)
                    }
                }
            }
        })
    }

    // region 点击事件
    fun clickBack(view: View) {
        finish()
    }
    // endregion

}