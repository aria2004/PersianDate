package com.amir.persiandate

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.icu.text.SimpleDateFormat
import android.os.Build
import java.util.Date
import java.util.Locale

object DateNotifier {
    private const val CHANNEL_ID = "persian_date"
    private const val NOTIFICATION_ID = 1401
    const val ACTION_RESTORE = "com.amir.persiandate.RESTORE_NOTIFICATION"

    fun show(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "نمایش تاریخ شمسی", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "نمایش دائمی روز و تاریخ شمسی"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }

        val day = SimpleDateFormat("d", Locale.forLanguageTag("fa-IR-u-ca-persian")).format(Date())
        val restoreIntent = Intent(context, DateReceiver::class.java).setAction(ACTION_RESTORE)
        val deletePendingIntent = PendingIntent.getBroadcast(
            context, 1402, restoreIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val openIntent = PendingIntent.getActivity(
            context, 1403, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }
        val notification = builder
            .setContentTitle("تاریخ امروز")
            .setContentText(DateText.today())
            .setSmallIcon(Icon.createWithBitmap(createDayIcon(day)))
            .setContentIntent(openIntent)
            .setDeleteIntent(deletePendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setShowWhen(false)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createDayIcon(day: String): Bitmap {
        val bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = if (day.length > 1) 33f else 39f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }
        val baseline = (canvas.height - (paint.ascent() + paint.descent())) / 2f
        canvas.drawText(day, canvas.width / 2f, baseline, paint)
        return bitmap
    }

    fun hide(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_ID)
    }
}
