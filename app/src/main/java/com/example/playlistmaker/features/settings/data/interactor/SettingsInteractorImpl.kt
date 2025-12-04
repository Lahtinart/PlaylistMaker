package com.example.playlistmaker.features.settings.data.interactor

import com.example.playlistmaker.features.settings.domain.interactor.ISettingsInteractor
import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : ISettingsInteractor {
    override fun isDarkThemeEnabled(): Boolean = settingsRepository.isDarkThemeEnabled()
    override fun setDarkThemeEnabled(enabled: Boolean) = settingsRepository.setDarkThemeEnabled(enabled)
}
