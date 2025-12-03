package com.example.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.features.settings.data.repository.SettingsRepositoryImpl

class App : Application() {

    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Инициализация репозитория настроек
        settingsRepository = SettingsRepositoryImpl(this)

        // Применяем тему при старте приложения
        if (settingsRepository.isDarkThemeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
