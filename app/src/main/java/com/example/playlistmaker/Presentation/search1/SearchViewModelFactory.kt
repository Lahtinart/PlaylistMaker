package com.example.playlistmaker.presentation.search1

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor

class SearchViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            // Репозиторий
            val repository = SearchRepositoryImpl()
            val searchInteractor = SearchTracksInteractor(repository)

            // SharedPreferences для истории
            val prefs = context.getSharedPreferences("playlist_prefs", Context.MODE_PRIVATE)
            val historyStorage = SearchHistoryStorage(prefs)
            val historyInteractor = HistoryInteractor(historyStorage)

            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchInteractor, historyInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

