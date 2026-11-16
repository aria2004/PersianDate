package com.amir.persiandate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (prefs.getBoolean("enabled", true)) DateNotifier.show(context)
    }
}
