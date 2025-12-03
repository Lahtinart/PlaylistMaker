package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractor(private val settingsRepository: SettingsRepository) {

    fun isDarkThemeEnabled(): Boolean = settingsRepository.isDarkThemeEnabled()

    fun setDarkThemeEnabled(enabled: Boolean) = settingsRepository.setDarkThemeEnabled(enabled)
}


