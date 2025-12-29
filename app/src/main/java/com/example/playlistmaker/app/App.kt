package com.example.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.features.settings.data.interactor.SettingsInteractorImpl
import com.example.playlistmaker.features.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.features.settings.domain.interactor.ISettingsInteractor
import com.example.playlistmaker.features.settings.domain.repository.SettingsRepository

class App : Application() {

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var settingsInteractor: ISettingsInteractor
        private set

    override fun onCreate() {
        super.onCreate()

        // Инициализация репозитория и интерактора
        settingsRepository = SettingsRepositoryImpl(this)
        settingsInteractor = SettingsInteractorImpl(settingsRepository)

        // Применяем тему при старте приложения
        if (settingsInteractor.isDarkThemeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
