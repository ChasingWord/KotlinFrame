package com.shrimp.base.utils.thread

/**
 * Created by chasing on 2021/10/22.
 */
interface Prioritized {
    /**
     * Returns the priority of this task.
     */
    fun getPriority(): Int
}