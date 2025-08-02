package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences

object SettingsManager {
    private const val PREFS_NAME = "settings"
    private const val DARK_THEME_KEY = "dark_theme"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isDarkThemeEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(DARK_THEME_KEY, false)
    }

    fun setDarkThemeEnabled(context: Context, isEnabled: Boolean) {
        getPrefs(context).edit().putBoolean(DARK_THEME_KEY, isEnabled).apply()
    }
}