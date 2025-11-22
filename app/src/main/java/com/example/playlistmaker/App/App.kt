package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl

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
