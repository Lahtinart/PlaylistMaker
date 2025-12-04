package com.example.playlistmaker.features.settings.domain.interactor

interface ISettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}
