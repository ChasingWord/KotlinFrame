package com.hebao.testkotlin.view.sub

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivitySecondBinding
import com.shrimp.base.adapter.recycler.BaseRecyclerAdapter
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
        statusBarColor = R.color.transparent
    }

    override fun getViewModelClass(): Class<SecondViewModel> = SecondViewModel::class.java

    override fun inflateDataBinding() = ActivitySecondBinding.inflate(layoutInflater)

    override fun initView() {
        dataBinding.vm = viewModel
        ImageLoadUtil.loadRound(
            dataBinding.imageView,
            "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
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

        secondAdapter.setItemClickListener(object : BaseRecyclerAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                val item = secondAdapter.getItem(position)
                viewModel.title.value = item.Name
            }
        })
    }

    override fun initDataObserve() {
        viewModel.data.observe(this) {
            secondAdapter.insertAll(it)
        }
    }

    // region 点击事件
    fun clickBack(view: View) {
        finish()
    }
    // endregion

}