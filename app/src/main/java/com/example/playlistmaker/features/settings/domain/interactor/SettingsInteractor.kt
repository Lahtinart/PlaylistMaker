package com.example.playlistmaker.features.settings.domain.interactor

import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository

class SettingsInteractor(private val settingsRepository: SettingsRepository) {

    fun isDarkThemeEnabled(): Boolean = settingsRepository.isDarkThemeEnabled()

    fun setDarkThemeEnabled(enabled: Boolean) = settingsRepository.setDarkThemeEnabled(enabled)
}


