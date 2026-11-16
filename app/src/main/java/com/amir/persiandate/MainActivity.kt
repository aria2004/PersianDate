package com.amir.persiandate

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Calendar

class MainActivity : Activity() {
    private val requestCode = 77
    private val ink = Color.rgb(28, 38, 58)
    private val muted = Color.rgb(112, 123, 143)
    private val accent = Color.rgb(73, 93, 220)
    private val surface = Color.WHITE
    private val page = Color.rgb(245, 247, 252)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val enabled = prefs.getBoolean("enabled", true)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(34), dp(24), dp(24))
            setBackgroundColor(page)
        }
        val header = TextView(this).apply {
            text = "تقویم کوچک تو"
            textSize = 15f
            setTextColor(muted)
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }
        val title = TextView(this).apply {
            text = "تاریخ شمسی"
            textSize = 29f
            setTextColor(ink)
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setPadding(0, dp(5), 0, dp(20))
        }
        val dateCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(20), dp(28), dp(20), dp(28))
            background = rounded(accent, 24)
        }
        val todayLabel = TextView(this).apply {
            text = "امروز"
            textSize = 14f
            setTextColor(Color.rgb(222, 227, 255))
            gravity = Gravity.CENTER
        }
        val date = TextView(this).apply {
            text = DateText.today()
            textSize = 27f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setPadding(0, dp(12), 0, dp(4))
        }
        dateCard.addView(todayLabel)
        dateCard.addView(date)

        val sectionTitle = TextView(this).apply {
            text = "نمایش در نوار اعلان"
            textSize = 18f
            setTextColor(ink)
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            setPadding(0, dp(26), 0, dp(10))
        }
        val infoCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            background = rounded(surface, 18)
        }
        val info = TextView(this).apply {
            text = "عدد روز کنار ساعت نمایش داده می‌شود و با شروع هر روز به‌روزرسانی خواهد شد."
            textSize = 14f
            setTextColor(muted)
            setLineSpacing(dp(4).toFloat(), 1f)
        }
        val status = TextView(this).apply {
            text = if (enabled) "●  نمایش تاریخ فعال است" else "○  نمایش تاریخ غیرفعال است"
            textSize = 13f
            setTextColor(if (enabled) Color.rgb(36, 145, 105) else muted)
            setPadding(0, dp(14), 0, 0)
        }
        infoCard.addView(info)
        infoCard.addView(status)

        val enable = Button(this).apply {
            text = "فعال‌سازی نمایش تاریخ"
            isAllCaps = false
            textSize = 15f
            setTextColor(Color.WHITE)
            background = rounded(accent, 14)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setOnClickListener {
                prefs.edit().putBoolean("enabled", true).apply()
                if (Build.VERSION.SDK_INT >= 33 && !canPostNotifications()) {
                    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode)
                } else {
                    DateNotifier.show(this@MainActivity)
                    scheduleDailyUpdate(this@MainActivity)
                    recreate()
                }
            }
        }
        val disable = Button(this).apply {
            text = "غیرفعال‌سازی اعلان"
            isAllCaps = false
            textSize = 14f
            setTextColor(muted)
            background = rounded(Color.rgb(232, 235, 243), 14)
            setOnClickListener {
                prefs.edit().putBoolean("enabled", false).apply()
                cancelDailyUpdate(this@MainActivity)
                DateNotifier.hide(this@MainActivity)
                recreate()
            }
        }
        val footer = TextView(this).apply {
            text = "ساده، سبک و همیشه در دسترس"
            textSize = 12f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(22), 0, 0)
        }

        root.addView(header)
        root.addView(title)
        root.addView(dateCard, LinearLayout.LayoutParams(-1, -2))
        root.addView(sectionTitle)
        root.addView(infoCard)
        root.addView(enable, LinearLayout.LayoutParams(-1, dp(52)).apply { topMargin = dp(20) })
        root.addView(disable, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(8) })
        root.addView(footer)
        setContentView(root)

        if (enabled) {
            if (canPostNotifications()) {
                DateNotifier.show(this)
                scheduleDailyUpdate(this)
            } else if (Build.VERSION.SDK_INT >= 33) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode)
            }
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
            recreate()
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun rounded(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
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
}
