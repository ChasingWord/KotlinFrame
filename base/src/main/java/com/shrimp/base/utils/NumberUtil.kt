package com.shrimp.base.utils

import java.util.*

/**
 * Created by chasing on 2021/10/22.
 */
object NumberUtil {
    /**
     * GUID是一个128位长的数字，一般用16进制表示。算法的核心思想是结合机器的网卡、当地时间、一个随即数来生成GUID,
     * 可以产生一个号称全球唯一的ID
     */
    fun generateGUID(): String {
        return UUID.randomUUID().toString()
    }
}