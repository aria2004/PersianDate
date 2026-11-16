package com.amir.persiandate

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateText {
    fun today(): String {
        val locale = Locale.forLanguageTag("fa-IR-u-ca-persian")
        val formatter = SimpleDateFormat("EEEE، d MMMM yyyy", locale)
        return formatter.format(Date())
    }
}
