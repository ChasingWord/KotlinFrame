package com.shrimp.base.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.lang.reflect.Field

/**
 * Created by chasing on 2021/10/22.
 */
object FixMemLeakUtil {
    private var field: Field? = null
    private var hasField = true

    //解决华为InputMethodManager.mLastSrvView造成的内存泄露
    fun fixLeak(context: Context?) {
        if (!hasField || context == null) {
            return
        }
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val arr = arrayOf("mLastSrvView")
        for (param in arr) {
            try {
                if (field == null) {
                    field = imm.javaClass.getDeclaredField(param)
                }
                field!!.isAccessible = true
                field!![imm] = null
            } catch (ignored: Throwable) {
                hasField = false
            }
        }
    }
}