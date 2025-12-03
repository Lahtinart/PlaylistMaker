package com.example.playlistmaker.di

import SearchHistoryRepositoryImpl
import android.content.Context
import com.example.playlistmaker.features.search.data.repository.SearchRepositoryImpl

import com.example.playlistmaker.features.search.domain.interactor.HistoryInteractor
import com.example.playlistmaker.features.search.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.features.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.features.search.domain.repository.SearchRepository
import com.google.gson.Gson

object ServiceLocator {

    private val gson by lazy { Gson() }

    // repo
    fun provideSearchRepository(): SearchRepository {
        return SearchRepositoryImpl()
    }

    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val prefs = context.getSharedPreferences("playlist_prefs", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(prefs, gson)
    }

    // interactors
    fun provideSearchInteractor(context: Context): SearchTracksInteractor {
        val repo = provideSearchRepository()
        return SearchTracksInteractor(repo)
    }

    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        val repo = provideSearchHistoryRepository(context)
        return HistoryInteractor(repo)
    }
}
