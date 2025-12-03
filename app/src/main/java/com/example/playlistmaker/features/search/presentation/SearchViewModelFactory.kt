package com.example.playlistmaker.features.search.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.di.ServiceLocator

class SearchViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {

            // получаем интеракторы из ServiceLocator (или создаём здесь, если хочешь)
            val searchInteractor = ServiceLocator.provideSearchInteractor(context)
            val historyInteractor = ServiceLocator.provideHistoryInteractor(context)

            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchInteractor, historyInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
