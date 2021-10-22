package com.shrimp.base.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by chasing on 2021/10/22.
 */
class NotificationClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val realIntent = intent.getParcelableExtra<Intent>("intent")
        context.startActivity(realIntent)
    }
}