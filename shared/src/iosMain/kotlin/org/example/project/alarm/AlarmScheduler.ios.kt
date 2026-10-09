package org.example.project.alarm

import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

actual fun scheduleDeviceAlarm(title: String, dateString: String, timeString: String) {
    try {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, error ->
            if (granted) {
                val content = UNMutableNotificationContent().apply {
                    setTitle("Vehicle Reminder: $title")
                    setBody(if (timeString.isNotBlank()) "Scheduled for $timeString ($dateString)" else "Reminder for $title")
                    setSound(UNNotificationSound.defaultSound())
                }

                val dateParts = dateString.trim().split(" ", "-", "/")
                val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                var day = 1L
                var month = 1L
                var year = 2026L

                if (dateParts.size >= 3) {
                    day = dateParts[0].toLongOrNull() ?: 1L
                    val mIdx = monthNames.indexOfFirst { it.equals(dateParts[1], ignoreCase = true) }
                    month = if (mIdx >= 0) (mIdx + 1).toLong() else (dateParts[1].toLongOrNull() ?: 1L)
                    year = dateParts[2].toLongOrNull() ?: 2026L
                }

                var hour = 9L
                var minute = 0L
                if (timeString.isNotBlank()) {
                    val isPm = timeString.contains("PM", ignoreCase = true)
                    val isAm = timeString.contains("AM", ignoreCase = true)
                    val cleanTime = timeString.replace("AM", "", ignoreCase = true).replace("PM", "", ignoreCase = true).trim()
                    val timeParts = cleanTime.split(":")
                    if (timeParts.isNotEmpty()) {
                        val h = timeParts[0].trim().toLongOrNull() ?: 9L
                        minute = timeParts.getOrNull(1)?.trim()?.toLongOrNull() ?: 0L
                        hour = when {
                            isPm && h < 12L -> h + 12L
                            isAm && h == 12L -> 0L
                            else -> h
                        }
                    }
                }

                val components = NSDateComponents().apply {
                    setYear(year)
                    setMonth(month)
                    setDay(day)
                    setHour(hour)
                    setMinute(minute)
                    setSecond(0)
                }

                val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(components, repeats = false)
                val identifier = "vehicle_status_${(title + dateString + timeString).hashCode()}"
                val request = UNNotificationRequest.requestWithIdentifier(identifier, content, trigger)

                center.addNotificationRequest(request) { reqError ->
                    if (reqError != null) {
                        println("[AlarmScheduler] iOS alarm scheduling failed: ${reqError.localizedDescription}")
                    } else {
                        println("[AlarmScheduler] iOS alarm scheduled for '$title' at $dateString $timeString")
                    }
                }
            } else {
                println("[AlarmScheduler] iOS notification permission not granted: ${error?.localizedDescription}")
            }
        }
    } catch (e: Exception) {
        println("[AlarmScheduler] Error scheduling iOS alarm: ${e.message}")
    }
}
