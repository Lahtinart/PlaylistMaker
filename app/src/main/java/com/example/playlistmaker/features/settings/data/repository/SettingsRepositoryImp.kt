package com.example.playlistmaker.features.settings.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override fun isDarkThemeEnabled(): Boolean =
        prefs.getBoolean("dark_theme", false)

    override fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("dark_theme", enabled).apply()
        // Применяем тему сразу через AppCompatDelegate
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}