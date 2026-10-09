package org.example.project.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import org.example.project.appContext
import java.util.Calendar

actual fun scheduleDeviceAlarm(title: String, dateString: String, timeString: String) {
    try {
        val calendar = parseDateAndTimeToCalendar(dateString, timeString)
        val triggerMillis = calendar.timeInMillis

        val intent = Intent(appContext, AlarmReceiver::class.java).apply {
            putExtra("alarm_title", title)
            putExtra("alarm_date", dateString)
            putExtra("alarm_time", timeString)
        }

        val requestCode = (title + dateString + timeString).hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
        println("[AlarmScheduler] Device alarm scheduled for '$title' at $dateString $timeString (epoch=$triggerMillis)")
    } catch (e: Exception) {
        println("[AlarmScheduler] Failed to schedule device alarm: ${e.message}")
        e.printStackTrace()
    }
}

private fun parseDateAndTimeToCalendar(dateString: String, timeString: String): Calendar {
    val calendar = Calendar.getInstance()
    val dateParts = dateString.trim().split(" ", "-", "/")
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    var day = calendar.get(Calendar.DAY_OF_MONTH)
    var month = calendar.get(Calendar.MONTH)
    var year = calendar.get(Calendar.YEAR)

    if (dateParts.size >= 3) {
        day = dateParts[0].toIntOrNull() ?: day
        val mIdx = monthNames.indexOfFirst { it.equals(dateParts[1], ignoreCase = true) }
        month = if (mIdx >= 0) mIdx else ((dateParts[1].toIntOrNull() ?: 1) - 1).coerceIn(0, 11)
        year = dateParts[2].toIntOrNull() ?: year
    }

    var hour = 9
    var minute = 0
    if (timeString.isNotBlank()) {
        val isPm = timeString.contains("PM", ignoreCase = true)
        val isAm = timeString.contains("AM", ignoreCase = true)
        val cleanTime = timeString.replace("AM", "", ignoreCase = true).replace("PM", "", ignoreCase = true).trim()
        val timeParts = cleanTime.split(":")
        if (timeParts.isNotEmpty()) {
            val h = timeParts[0].trim().toIntOrNull() ?: 9
            minute = timeParts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
            hour = when {
                isPm && h < 12 -> h + 12
                isAm && h == 12 -> 0
                else -> h
            }
        }
    }

    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar
}
