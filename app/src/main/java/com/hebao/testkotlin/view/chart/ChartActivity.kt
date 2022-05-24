package com.hebao.testkotlin.view.chart

import android.content.Context
import com.hebao.testkotlin.databinding.ActivityChartBinding
import com.hebao.testkotlin.widget.ChartBean
import com.shrimp.base.view.BaseActivity

/**
 * Created by chasing on 2022/3/1.
 */
class ChartActivity : BaseActivity<ChartViewModel, ActivityChartBinding>() {

    companion object {
        fun start(context: Context) {
            start(context, ChartActivity::class.java)
        }
    }

    override fun inflateDataBinding(): ActivityChartBinding =
        ActivityChartBinding.inflate(layoutInflater)

    override fun getViewModelClass(): Class<ChartViewModel> = ChartViewModel::class.java

    override fun initView() {
        val android = ArrayList<ChartBean>()
        var daily = ChartBean("12.30", 500)
        android.add(daily)
        daily = ChartBean("1.5", 512)
        android.add(daily)
        daily = ChartBean("1.10", 524)
        android.add(daily)
        daily = ChartBean("1.19", 446)
        android.add(daily)
        daily = ChartBean("1.25", 378)
        android.add(daily)
        daily = ChartBean("2.7", 379)
        android.add(daily)
        daily = ChartBean("2.14", 511)
        android.add(daily)
        daily = ChartBean("2.22", 509)
        android.add(daily)
        daily = ChartBean("2.28", 521)
        android.add(daily)
        daily = ChartBean("3.7", 574)
        android.add(daily)
        daily = ChartBean("3.14", 543)
        android.add(daily)
        daily = ChartBean("3.21", 543)
        android.add(daily)
        daily = ChartBean("3.28", 584)
        android.add(daily)
        daily = ChartBean("4.6", 544)
        android.add(daily)
        daily = ChartBean("4.11", 535)
        android.add(daily)
        daily = ChartBean("4.18", 384)
        android.add(daily)
        daily = ChartBean("4.25", 300)
        android.add(daily)
        daily = ChartBean("5.09", 318)
        android.add(daily)
        daily = ChartBean("5.16", 264)
        android.add(daily)

        val ios = ArrayList<ChartBean>()
        daily = ChartBean("1.19", 199)
        ios.add(daily)
        daily = ChartBean("1.25", 173)
        ios.add(daily)
        daily = ChartBean("2.7", 175)
        ios.add(daily)
        daily = ChartBean("2.14", 210)
        ios.add(daily)
        daily = ChartBean("2.22", 206)
        ios.add(daily)
        daily = ChartBean("2.28", 222)
        ios.add(daily)
        daily = ChartBean("3.7", 231)
        ios.add(daily)
        daily = ChartBean("3.14", 196)
        ios.add(daily)
        daily = ChartBean("3.21", 224)
        ios.add(daily)
        daily = ChartBean("3.28", 200)
        ios.add(daily)
        daily = ChartBean("4.6", 213)
        ios.add(daily)
        daily = ChartBean("4.11", 242)
        ios.add(daily)
        daily = ChartBean("4.18", 210)
        ios.add(daily)
        daily = ChartBean("4.25", 198)
        ios.add(daily)
        daily = ChartBean("5.09", 227)
        ios.add(daily)
        daily = ChartBean("5.16", 243)
        ios.add(daily)

        dataBinding.chart.addChartBeanList("android", android)
        dataBinding.chart.addChartBeanList("ios", ios)
        dataBinding.chart.isScreenVertical = true
        dataBinding.button.setOnClickListener {
            dataBinding.chart.isScreenVertical = !dataBinding.chart.isScreenVertical
        }

        dataBinding.chart.isEnabled

        val a = mutableListOf<Int>()
        a.add(1)

    }

    override fun initDataObserve() {
    }
}