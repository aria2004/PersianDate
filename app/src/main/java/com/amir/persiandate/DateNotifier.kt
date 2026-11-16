package com.amir.persiandate

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object DateNotifier {
    private const val CHANNEL_ID = "persian_date"
    private const val NOTIFICATION_ID = 1401

    fun show(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "نمایش تاریخ شمسی", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "تاریخ روزانه شمسی"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }.setContentTitle("امروز")
            .setContentText(DateText.today())
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
            .setOngoing(true)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setShowWhen(false)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    fun hide(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_ID)
    }
}
