package org.example.project

expect fun getCurrentDateString(): String

fun addDaysToDate(dateString: String, daysOffset: Int): String {
    val epochDays = getEpochDays(dateString)
    return dateStringFromEpochDays(epochDays + daysOffset)
}

fun parseDateParts(dateString: String): Triple<Int, Int, Int>? {
    val trimmed = dateString.trim()
    if (trimmed.isEmpty()) return null

    val dateOnly = if (trimmed.contains("T")) {
        trimmed.substringBefore("T")
    } else {
        val parts = trimmed.split(" ")
        if (parts.size >= 2 && parts[0].contains(Regex("[-/]"))) {
            parts[0]
        } else {
            trimmed
        }
    }

    val monthNames = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")

    val tokensWithText = dateOnly.split(Regex("[\\s,/-]+")).filter { it.isNotBlank() }
    if (tokensWithText.size >= 3) {
        val mIdx = tokensWithText.indexOfFirst { token ->
            monthNames.any { token.lowercase().startsWith(it) }
        }
        if (mIdx >= 0) {
            val month = monthNames.indexOfFirst { tokensWithText[mIdx].lowercase().startsWith(it) } + 1
            val otherNums = tokensWithText.filterIndexed { index, _ -> index != mIdx }.mapNotNull { it.toIntOrNull() }
            if (otherNums.size >= 2) {
                val year = if (otherNums[0] > 1000) otherNums[0] else otherNums[1]
                val day = if (otherNums[0] > 1000) otherNums[1] else otherNums[0]
                return Triple(year, month, day)
            }
        }
    }

    val numParts = dateOnly.split(Regex("[^0-9]+")).filter { it.isNotBlank() }.mapNotNull { it.toIntOrNull() }
    if (numParts.size < 3) return null

    val p0 = numParts[0]
    val p1 = numParts[1]
    val p2 = numParts[2]

    return if (p0 > 1000) {
        Triple(p0, p1.coerceIn(1, 12), p2.coerceIn(1, 31))
    } else if (p2 > 1000) {
        Triple(p2, p1.coerceIn(1, 12), p0.coerceIn(1, 31))
    } else {
        val year = 2000 + p2
        Triple(year, p1.coerceIn(1, 12), p0.coerceIn(1, 31))
    }
}

fun getEpochDays(dateString: String): Int {
    return try {
        val parsed = parseDateParts(dateString) ?: return 0
        val year = parsed.first
        val month = parsed.second
        val day = parsed.third

        val daysInMonths = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        var days = 0
        for (y in 2020 until year) {
            days += if ((y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)) 366 else 365
        }
        for (y in year until 2020) {
            days -= if ((y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)) 366 else 365
        }

        val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        for (m in 1 until month) {
            days += if (m == 2 && isLeapYear) 29 else daysInMonths[m - 1]
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
    val parsed = parseDateParts(dateStr)
    if (parsed != null) {
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthStr = monthNames[(parsed.second - 1).coerceIn(0, 11)]
        return "$monthStr ${parsed.first}"
    }
    return dateStr
}

fun getFullMonthHeader(dateStr: String): String {
    val parsed = parseDateParts(dateStr)
    if (parsed != null) {
        val monthFullNames = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val monthStr = monthFullNames[(parsed.second - 1).coerceIn(0, 11)]
        return "$monthStr ${parsed.first}"
    }
    return dateStr
}

fun formatDisplayDate(dateStr: String): String {
    return formatStandardDate(dateStr)
}

fun formatStandardDate(dateStr: String): String {
    val parsed = parseDateParts(dateStr)
    if (parsed != null) {
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val dayStr = parsed.third.toString().padStart(2, '0')
        val monthStr = monthNames[(parsed.second - 1).coerceIn(0, 11)]
        return "$dayStr $monthStr ${parsed.first}"
    }
    return dateStr
}

fun getTimelineDayMonth(dateStr: String): String {
    val parsed = parseDateParts(dateStr)
    if (parsed != null) {
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val dayStr = parsed.third.toString().padStart(2, '0')
        val monthStr = monthNames[(parsed.second - 1).coerceIn(0, 11)]
        return "$dayStr $monthStr"
    }
    return dateStr
}

fun getTodayMarkerText(dateStr: String): String {
    return formatStandardDate(dateStr)
}

fun dateStringFromUtcMillis(millis: Long): String {
    val epochDay1970 = (millis / 86_400_000L).toInt()
    val customEpochDays2020 = epochDay1970 - 18262 + 1
    return dateStringFromEpochDays(customEpochDays2020)
}

fun utcMillisFromDateString(dateString: String): Long? {
    val customDays = getEpochDays(dateString)
    if (customDays <= 0) return null
    val epochDays1970 = (customDays - 1 + 18262).toLong()
    return epochDays1970 * 86_400_000L
}
