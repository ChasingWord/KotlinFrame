package com.shrimp.base.utils

/**
 * Created by chasing on 2021/10/22.
 */
object CollectionUtil {
    fun isEmpty(collection: Collection<*>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    fun isNotEmpty(collection: Collection<*>?): Boolean {
        return collection != null && collection.isNotEmpty()
    }
}
