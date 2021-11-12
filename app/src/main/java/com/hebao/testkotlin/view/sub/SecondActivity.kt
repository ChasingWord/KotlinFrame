package com.hebao.testkotlin.view.sub

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivitySecondBinding
import com.shrimp.base.decoration.HorizontalDividerItemDecoration
import com.shrimp.base.view.BaseActivity
import com.shrimp.network.entity.res.Tags
import java.util.*

/**
 * Created by chasing on 2021/10/25.
 * 测试网络请求、RecyclerView-Adapter-MultiItemType样式
 * 在Repository测试数据库Dao存储
 */
class SecondActivity : BaseActivity<SecondViewModel, ActivitySecondBinding>() {

    private val secondAdapter = SecondAdapter(context)

    companion object {
        fun start(context: Context) {
            start(context, SecondActivity::class.java)
        }
    }

    override fun changeConfig() {
        needChangeStatusBar = false
    }

    override fun getViewModelClass(): Class<SecondViewModel> = SecondViewModel::class.java

    override fun inflateDataBinding() = ActivitySecondBinding.inflate(layoutInflater)

    override fun initView() {
        dataBinding.vm = baseViewModel

        dataBinding.rcv.layoutManager = LinearLayoutManager(context)
        dataBinding.rcv.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(context).marginResId(R.dimen.fab_margin)
                .colorResId(
                    R.color.teal_700
                ).build()
        )
        dataBinding.rcv.adapter = secondAdapter
    }

    override fun initDataObserve() {
        baseViewModel.data.observe(this, {
            secondAdapter.insertAll(it)
        })
    }

    // region 点击事件
    fun clickBack(view: View) {
        finish()
    }
    // endregion

}