package com.shrimp.base.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.shrimp.base.BaseApplication

/**
 * Created by chasing on 2022/7/4.
 */
fun showToast(@StringRes stringId: Int, context: Context = BaseApplication.CONTEXT) {
    Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show()
}

fun showToast(message: String?, context: Context = BaseApplication.CONTEXT) {
    if (message != null)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}