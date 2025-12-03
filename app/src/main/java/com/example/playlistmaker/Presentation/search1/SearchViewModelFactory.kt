package com.example.playlistmaker.presentation.search1

import SearchHistoryRepositoryImpl
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.google.gson.Gson

class SearchViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {

            // API репозиторий
            val searchRepository = SearchRepositoryImpl()
            val searchInteractor = SearchTracksInteractor(searchRepository)

            // История поиска — новый репозиторий
            val prefs = context.getSharedPreferences("playlist_prefs", Context.MODE_PRIVATE)
            val gson = Gson()
            val historyRepository = SearchHistoryRepositoryImpl(prefs, gson)
            val historyInteractor = HistoryInteractor(historyRepository)

            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchInteractor, historyInteractor) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

