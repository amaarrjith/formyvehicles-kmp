package org.example.project.data.settings

open class AppPreferences {
    private var language: String = "en"

    open fun getLanguage(): String = language
    open fun setLanguage(language: String) {
        this.language = language
    }
}
