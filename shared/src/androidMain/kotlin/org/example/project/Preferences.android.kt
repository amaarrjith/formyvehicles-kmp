package org.example.project

import android.content.Context

lateinit var appContext: Context

actual fun isUserLoggedIn(): Boolean {
    val sharedPrefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean("user_is_logged_in", false)
}

actual fun setUserLoggedIn(isLoggedIn: Boolean) {
    val sharedPrefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean("user_is_logged_in", isLoggedIn).apply()
}

actual fun isGuestUser(): Boolean {
    val sharedPrefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean("is_guest_user", false)
}

actual fun setGuestUser(isGuest: Boolean) {
    val sharedPrefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean("is_guest_user", isGuest).apply()
}

actual fun getPersistedString(key: String): String? {
    val sharedPrefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPrefs.getString(key, null)
}

actual fun setPersistedString(key: String, value: String?) {
    val sharedPrefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPrefs.edit().putString(key, value).apply()
}
