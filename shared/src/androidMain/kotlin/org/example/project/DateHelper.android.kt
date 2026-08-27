package org.example.project

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

actual fun getCurrentDateString(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
    return sdf.format(Calendar.getInstance().time)
}
