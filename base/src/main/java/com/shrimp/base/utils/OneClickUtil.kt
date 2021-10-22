package com.shrimp.base.utils

import java.util.*

/**
 * Created by chasing on 2021/10/22.
 * 防抖动连续点击的辅助类
 * 每个类都包含自己一个OneClickUtil，
 * 避免mLastClickTimeMap包含太多不必要的数据，
 * 同时避免两个类的methodName相同
 */
class OneClickUtil {
    private val minClickDelayTime = 500

    private lateinit var lastClickTimeMap: MutableMap<String, Long>

    fun OneClickUtil() {
        lastClickTimeMap = HashMap()
    }

    /**
     * 检测是否连续单击了两次
     *
     * @param id 控件id，R文件内控件id都是唯一的
     * @return true为短时间内连续单击两次，false不是
     */
    fun check(id: Int): Boolean {
        return check(id.toString())
    }

    //singleSign能代表唯一标识即可
    fun check(singleSign: String): Boolean {
        return check(singleSign, minClickDelayTime.toLong())
    }

    fun check(singleSign: String, delayTime: Long): Boolean {
        val currentTime = Calendar.getInstance().timeInMillis
        return if (!lastClickTimeMap.containsKey(singleSign)) {
            lastClickTimeMap[singleSign] = currentTime
            false
        } else {
            val preTime = lastClickTimeMap[singleSign]
            if (preTime != null) {
                if (currentTime - preTime > delayTime) {
                    lastClickTimeMap[singleSign] = currentTime
                    false
                } else {
                    true
                }
            } else false
        }
    }
}