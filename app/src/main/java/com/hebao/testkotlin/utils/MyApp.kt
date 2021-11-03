package com.hebao.testkotlin.utils

import android.app.Application
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shrimp.base.widgets.refresh.SmartRefreshFooter
import com.shrimp.base.widgets.refresh.SmartRefreshHeader


/**
 * Created by chasing on 2021/11/3.
 */
class MyApp : Application() {
    init {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> SmartRefreshHeader(context) }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> SmartRefreshFooter(context) }
    }
}