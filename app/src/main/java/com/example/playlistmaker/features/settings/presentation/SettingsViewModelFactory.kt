package com.example.playlistmaker.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.features.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository

class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val interactor = SettingsInteractor(settingsRepository) // создаём интерактор
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(interactor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

