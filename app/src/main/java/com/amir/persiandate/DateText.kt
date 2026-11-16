package com.amir.persiandate

import android.icu.util.PersianCalendar
import java.util.Locale

object DateText {
    private val weekdays = arrayOf("یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه")
    private val months = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    fun today(): String {
        val c = PersianCalendar()
        val dayName = weekdays[c.get(PersianCalendar.DAY_OF_WEEK) - 1]
        val day = c.get(PersianCalendar.DAY_OF_MONTH)
        val month = months[c.get(PersianCalendar.MONTH)]
        val year = c.get(PersianCalendar.YEAR)
        return "$dayName، $day $month $year"
            .map { if (it in '0'..'9') ('۰'.code + it.code - '0'.code).toChar() else it }
            .joinToString("")
    }
}
