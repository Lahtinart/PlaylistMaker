package com.example.playlistmaker.features.settings.domain.repository

interface SettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}