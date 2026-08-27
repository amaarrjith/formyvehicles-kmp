package org.example.project

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

actual fun getCurrentDateString(): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "dd MMM yyyy"
        locale = NSLocale("en_US")
    }
    return formatter.stringFromDate(NSDate())
}
