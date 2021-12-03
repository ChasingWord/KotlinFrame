package com.hebao.testkotlin.view.sub

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivitySecondBinding
import com.shrimp.base.decoration.HorizontalDividerItemDecoration
import com.shrimp.base.utils.image_load.ImageLoadUtil
import com.shrimp.base.view.BaseActivity

/**
 * Created by chasing on 2021/10/25.
 * 测试网络请求、RecyclerView-Adapter-MultiItemType样式
 * 在Repository测试数据库Dao存储
 */
class SecondActivity : BaseActivity<SecondViewModel, ActivitySecondBinding>() {

    private lateinit var secondAdapter: SecondAdapter

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
        ImageLoadUtil.loadRound(
            dataBinding.imageView,
            "https://ts1.cn.mm.bing.net/th?id=OIP-C.ED2CV_IKkq4cCWrD3kxgQQHaJ7&w=123&h=170&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2",
            20f
        )

        dataBinding.rcv.layoutManager = LinearLayoutManager(context)
        dataBinding.rcv.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(context).marginResId(R.dimen.fab_margin)
                .colorResId(
                    R.color.teal_700
                ).build()
        )
        secondAdapter = SecondAdapter(context)
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