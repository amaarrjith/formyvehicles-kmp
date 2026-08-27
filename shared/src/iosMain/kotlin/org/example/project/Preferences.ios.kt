package org.example.project

import platform.Foundation.NSUserDefaults

actual fun isUserLoggedIn(): Boolean {
    return NSUserDefaults.standardUserDefaults.boolForKey("user_is_logged_in")
}

actual fun setUserLoggedIn(isLoggedIn: Boolean) {
    NSUserDefaults.standardUserDefaults.setBool(isLoggedIn, forKey = "user_is_logged_in")
}

actual fun isGuestUser(): Boolean {
    return NSUserDefaults.standardUserDefaults.boolForKey("is_guest_user")
}

actual fun setGuestUser(isGuest: Boolean) {
    NSUserDefaults.standardUserDefaults.setBool(isGuest, forKey = "is_guest_user")
}

actual fun getPersistedString(key: String): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(key)
}

actual fun setPersistedString(key: String, value: String?) {
    if (value == null) {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(key)
    } else {
        NSUserDefaults.standardUserDefaults.setObject(value, forKey = key)
    }
}
