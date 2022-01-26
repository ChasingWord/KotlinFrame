package com.hebao.testkotlin.view.sub

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.hebao.testkotlin.R
import com.hebao.testkotlin.databinding.ActivitySecondBinding
import com.shrimp.base.adapter.recycler.BaseRecyclerAdapter
import com.shrimp.base.decoration.HorizontalDividerItemDecoration
import com.shrimp.base.utils.StatusBarUtil
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
        dataBinding.vm = baseViewModel
        ImageLoadUtil.loadRound(
            dataBinding.imageView,
            "https://video.qiantucdn.com/58pic/35/05/76/13M58PIC8ZBtfVeCQQ58PICVp.mp4_10s.mp4?e=1638860299&token=OyzEe_0O8H433pm7zVEjtnSy5dVdfpsIawO2nx3f:WGmGvkra_yHhsX7wnm8hjZ_2mB0=",
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
                baseViewModel.title.value = item.Name
            }
        })

        // 添加生命周期监听
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
            }
        })
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