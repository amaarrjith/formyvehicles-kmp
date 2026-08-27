package org.example.project

expect fun getCurrentDateString(): String

fun addDaysToDate(dateString: String, daysOffset: Int): String {
    val epochDays = getEpochDays(dateString)
    return dateStringFromEpochDays(epochDays + daysOffset)
}

fun getEpochDays(dateString: String): Int {
    return try {
        val parts = dateString.trim().split(" ", "/")
        if (parts.size < 3) return 0
        val day = parts[0].toIntOrNull() ?: 1
        val monthStr = parts[1]
        val year = parts[2].toIntOrNull() ?: 2025
        
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthIdx = monthNames.indexOfFirst { it.equals(monthStr, ignoreCase = true) }
        val month = if (monthIdx >= 0) monthIdx else ((monthStr.toIntOrNull() ?: 1) - 1).coerceIn(0, 11)
        
        val daysInMonths = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        
        var days = 0
        for (y in 2020 until year) {
            days += if ((y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)) 366 else 365
        }
        for (m in 0 until month) {
            if (m == 1 && ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))) {
                days += 29
            } else {
                days += daysInMonths[m]
            }
        }
        days += day
        days
    } catch (e: Exception) {
        0
    }
}

fun dateStringFromEpochDays(epochDays: Int): String {
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val daysInMonths = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    
    var remainingDays = epochDays
    var year = 2020
    
    while (true) {
        val daysInYear = if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 366 else 365
        if (remainingDays > daysInYear) {
            remainingDays -= daysInYear
            year++
        } else {
            break
        }
    }
    
    var month = 0
    while (month < 12) {
        val dim = if (month == 1 && ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))) 29 else daysInMonths[month]
        if (remainingDays > dim) {
            remainingDays -= dim
            month++
        } else {
            break
        }
    }
    
    val day = if (remainingDays <= 0) 1 else remainingDays
    val monthStr = monthNames[month.coerceIn(0, 11)]
    val dayStr = day.toString().padStart(2, '0')
    
    return "$dayStr $monthStr $year"
}

fun getMonthHeader(dateStr: String): String {
    val parts = dateStr.trim().split(" ", "/")
    return if (parts.size >= 3) {
        val monthStr = parts[1]
        val yearStr = parts[2]
        "$monthStr $yearStr"
    } else {
        dateStr
    }
}
