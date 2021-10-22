package com.shrimp.base.utils.thread

import androidx.annotation.NonNull

/**
 * Created by chasing on 2021/10/22.
 */
abstract class ComparableRunnable : Runnable, Comparable<Any> {
    override fun compareTo(@NonNull other: Any): Int {
        if (hashCode() == other.hashCode()) return 0
        return if (hashCode() > other.hashCode()) 1 else -1
    }
}