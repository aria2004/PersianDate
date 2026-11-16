package com.amir.persiandate

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Calendar

class MainActivity : Activity() {
    private val requestCode = 77

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 48, 32, 48)
        }
        val title = TextView(this).apply {
            text = "تاریخ امروز"
            textSize = 22f
            gravity = Gravity.CENTER
        }
        val date = TextView(this).apply {
            text = DateText.today()
            textSize = 25f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 40)
        }
        val enable = Button(this).apply {
            text = "فعال‌سازی نمایش در اعلان"
            setOnClickListener { enableDate(prefs) }
        }
        val disable = Button(this).apply {
            text = "غیرفعال‌سازی"
            setOnClickListener {
                prefs.edit().putBoolean("enabled", false).apply()
                cancelDailyUpdate()
                DateNotifier.hide(this@MainActivity)
            }
        }
        root.addView(title)
        root.addView(date)
        root.addView(enable)
        root.addView(disable)
        setContentView(root)

        if (prefs.getBoolean("enabled", true)) {
            if (canPostNotifications()) {
                DateNotifier.show(this)
                scheduleDailyUpdate(this)
            } else if (Build.VERSION.SDK_INT >= 33) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode)
            }
        }
    }

    private fun enableDate(prefs: android.content.SharedPreferences) {
        prefs.edit().putBoolean("enabled", true).apply()
        if (Build.VERSION.SDK_INT >= 33 && !canPostNotifications()) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode)
        } else {
            DateNotifier.show(this)
            scheduleDailyUpdate(this)
        }
    }

    private fun canPostNotifications(): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            DateNotifier.show(this)
            scheduleDailyUpdate(this)
        }
    }

    companion object {
        fun scheduleDailyUpdate(context: Context) {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, DateReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val nextMidnight = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 5)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            alarm.setInexactRepeating(
                AlarmManager.RTC_WAKEUP, nextMidnight,
                AlarmManager.INTERVAL_DAY, pending
            )
        }

        private fun cancelDailyUpdate(context: Context) {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, DateReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarm.cancel(pending)
        }
    }

    private fun cancelDailyUpdate() = cancelDailyUpdate(this)
}
