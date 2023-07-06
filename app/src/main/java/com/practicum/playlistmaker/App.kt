package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator

const val APP_PREFERENCES = "app_preferences"

class App : Application() {
    val appPreferences: SharedPreferences by lazy {
        getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()

        val settingsInteractor = Creator.provideSettingsInteractorImpl(appPreferences)
        AppCompatDelegate.setDefaultNightMode(
            if (settingsInteractor.getThemeSettings().isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}