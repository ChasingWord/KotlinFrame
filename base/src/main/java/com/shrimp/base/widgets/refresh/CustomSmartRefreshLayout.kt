package com.shrimp.base.widgets.refresh

import android.content.Context
import android.util.AttributeSet
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState

/**
 * Created by chasing on 2021/11/3.
 */
class CustomSmartRefreshLayout : SmartRefreshLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context,  attrs: AttributeSet?):super(context, attrs)

    init{
        setEnableLoadMoreWhenContentNotFull(false)
    }

    // 设置正在刷新
    fun setRefreshing() {
        // 刷新中或者加载更多中都不能再进行刷新操作
        // 不能同时进行两次加载，否则请求回来之后状态会错乱
        if (mState == RefreshState.Loading) finishLoadMore()
        if (mState != RefreshState.Refreshing) setStateRefreshing(true)
    }

    override fun isRefreshing(): Boolean {
        return mState == RefreshState.Refreshing
    }
}