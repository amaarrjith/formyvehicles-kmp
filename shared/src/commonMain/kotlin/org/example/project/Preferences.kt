package org.example.project

expect fun isUserLoggedIn(): Boolean
expect fun setUserLoggedIn(isLoggedIn: Boolean)

expect fun isGuestUser(): Boolean
expect fun setGuestUser(isGuest: Boolean)

expect fun getPersistedString(key: String): String?
expect fun setPersistedString(key: String, value: String?)
