package com.example.playlistmaker.features.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.features.settings.domain.interactor.ISettingsInteractor

class SettingsViewModel(private val settingsInteractor: ISettingsInteractor) : ViewModel() {

    private val _state = MutableLiveData(SettingsState())
    val state: LiveData<SettingsState> = _state

    fun loadTheme() {
        val enabled = settingsInteractor.isDarkThemeEnabled()
        _state.value = _state.value?.copy(isDarkTheme = enabled)
    }

    fun toggleTheme(enabled: Boolean) {
        settingsInteractor.setDarkThemeEnabled(enabled)
        _state.value = _state.value?.copy(isDarkTheme = enabled)
    }
}
