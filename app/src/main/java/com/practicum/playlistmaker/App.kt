package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val APP_PREFERENCES = "app_preferences"

class App : Application() {
    lateinit var appPreferences: SharedPreferences
    var darkTheme = false
    
    override fun onCreate() {
        super.onCreate()

        appPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        darkTheme = appPreferences.getBoolean(DARK_THEME_KEY, false)
        setAppDarkTheme(darkTheme)
    }

    fun setAppDarkTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun saveAppPreferences(key: String, value: Boolean) {
        appPreferences.edit()
            .putBoolean(key, value)
            .apply()
    }


    companion object {
        const val DARK_THEME_KEY = "dark_theme"
        const val SEARCH_HISTORY_TRACKS = "search_history_tracks"
    }
}