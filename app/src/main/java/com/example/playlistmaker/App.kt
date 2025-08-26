package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private lateinit var sharedPrefs: SharedPreferences
    var darkTheme: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences("settings", MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean("dark_theme", false)

        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        // сохраняем настройку
        sharedPrefs.edit().putBoolean("dark_theme", darkTheme).apply()

        // применяем тему
        applyTheme(darkTheme)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}