package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.storage.SettingsManager

class App : Application() {

    var darkTheme: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()
        // Чтение темы через SettingsManager
        darkTheme = SettingsManager.isDarkThemeEnabled(this)
        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        SettingsManager.setDarkThemeEnabled(this, darkTheme)
        applyTheme(darkTheme)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
