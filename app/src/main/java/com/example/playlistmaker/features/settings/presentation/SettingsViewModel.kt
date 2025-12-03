package com.example.playlistmaker.features.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    fun loadTheme() {
        _isDarkTheme.value = settingsRepository.isDarkThemeEnabled()
    }

    fun toggleTheme(enabled: Boolean) {
        settingsRepository.setDarkThemeEnabled(enabled)
        _isDarkTheme.value = enabled
    }
}