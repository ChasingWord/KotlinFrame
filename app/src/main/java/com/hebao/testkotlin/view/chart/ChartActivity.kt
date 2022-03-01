package com.hebao.testkotlin.view.chart

import android.content.Context
import com.hebao.testkotlin.databinding.ActivityChartBinding
import com.hebao.testkotlin.widget.DailyInfo
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
        val android = ArrayList<DailyInfo>()
        var daily = DailyInfo("12.30", 500)
        android.add(daily)
        daily = DailyInfo("1.5", 512)
        android.add(daily)
        daily = DailyInfo("1.10", 524)
        android.add(daily)
        daily = DailyInfo("1.19", 446)
        android.add(daily)
        daily = DailyInfo("1.25", 378)
        android.add(daily)
        daily = DailyInfo("2.7", 379)
        android.add(daily)
        daily = DailyInfo("2.14", 511)
        android.add(daily)
        daily = DailyInfo("2.22", 509)
        android.add(daily)
        daily = DailyInfo("2.28", 521)
        android.add(daily)

        val ios = ArrayList<DailyInfo>()
        daily = DailyInfo("1.19", 199)
        ios.add(daily)
        daily = DailyInfo("1.25", 173)
        ios.add(daily)
        daily = DailyInfo("2.7", 175)
        ios.add(daily)
        daily = DailyInfo("2.14", 210)
        ios.add(daily)
        daily = DailyInfo("2.22", 206)
        ios.add(daily)
        daily = DailyInfo("2.28", 222)
        ios.add(daily)

        dataBinding.chart.setAndroidDailyInfo(android)
        dataBinding.chart.setIOSDailyInfo(ios)
        dataBinding.chart.refresh()
    }

    override fun initDataObserve() {
    }
}