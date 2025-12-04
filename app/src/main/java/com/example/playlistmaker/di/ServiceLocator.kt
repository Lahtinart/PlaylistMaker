package com.example.playlistmaker.di

import SearchHistoryRepositoryImpl
import android.content.Context
import com.example.playlistmaker.features.search.data.interactor.HistoryInteractorImpl
import com.example.playlistmaker.features.search.data.interactor.SearchTracksInteractorImpl
import com.example.playlistmaker.features.search.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.features.search.domain.interactor.IHistoryInteractor
import com.example.playlistmaker.features.search.domain.interactor.ISearchTracksInteractor
import com.example.playlistmaker.features.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.features.search.domain.repository.SearchRepository
import com.google.gson.Gson

object ServiceLocator {

    private val gson by lazy { Gson() }

    // Repositories
    fun provideSearchRepository(): SearchRepository {
        return SearchRepositoryImpl()
    }

    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val prefs = context.getSharedPreferences("playlist_prefs", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(prefs, gson)
    }

    // Interactors
    fun provideSearchInteractor(context: Context): ISearchTracksInteractor {
        val repo = provideSearchRepository()
        return SearchTracksInteractorImpl(repo) // возвращаем тип интерфейса
    }

    fun provideHistoryInteractor(context: Context): IHistoryInteractor {
        val repo = provideSearchHistoryRepository(context)
        return HistoryInteractorImpl(repo) // возвращаем тип интерфейса
    }
}
