package com.example.playlistmaker.domain.interactor

import android.content.Context
import com.example.playlistmaker.data.storage.SettingsManager

class SettingsInteractor(private val context: Context) {

    private val settingsManager = SettingsManager

    fun isDarkThemeEnabled(): Boolean = settingsManager.isDarkThemeEnabled(context)

    fun setDarkThemeEnabled(enabled: Boolean) = settingsManager.setDarkThemeEnabled(context, enabled)
}

