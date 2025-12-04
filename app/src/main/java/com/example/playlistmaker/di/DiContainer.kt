package com.example.playlistmaker.di

import com.example.playlistmaker.features.player.data.interactor.PlayerInteractorImpl
import com.example.playlistmaker.features.player.domain.interactor.IPlayerInteractor

object DiContainer {
    // lazy singleton возвращает интерфейс
    val playerInteractor: IPlayerInteractor by lazy {
        PlayerInteractorImpl()
    }
}

