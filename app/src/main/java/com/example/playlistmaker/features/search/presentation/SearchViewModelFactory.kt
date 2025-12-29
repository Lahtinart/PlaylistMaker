package com.example.playlistmaker.features.search.presentation

import SearchHistoryRepositoryImpl
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.features.search.data.interactor.HistoryInteractorImpl
import com.example.playlistmaker.features.search.data.interactor.SearchTracksInteractorImpl
import com.example.playlistmaker.features.search.data.repository.SearchRepositoryImpl
import com.google.gson.Gson

class SearchViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {

            val searchRepo = SearchRepositoryImpl()

            // SharedPreferences
            val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)

            // Gson
            val gson = Gson()

            // Репозиторий истории
            val historyRepo = SearchHistoryRepositoryImpl(prefs, gson)

            // Интеракторы
            val searchInteractor = SearchTracksInteractorImpl(searchRepo)
            val historyInteractor = HistoryInteractorImpl(historyRepo)

            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchInteractor, historyInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
