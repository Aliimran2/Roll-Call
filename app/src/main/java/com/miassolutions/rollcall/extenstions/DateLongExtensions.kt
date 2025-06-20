package com.miassolutions.rollcall.extenstions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDate(
    pattern: String = "dd/MM/yyyy",
    locale: Locale = Locale.getDefault(),
): String {
    return SimpleDateFormat(pattern, locale).format(Date(this))
}