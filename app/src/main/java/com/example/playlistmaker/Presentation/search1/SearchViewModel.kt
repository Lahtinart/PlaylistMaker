package com.example.playlistmaker.presentation.search1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchTracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    var searchText: String = ""

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun search() {
        if (searchText.isBlank()) return

        _loading.value = true
        _error.value = false

        viewModelScope.launch {
            try {
                val results = searchInteractor.execute(searchText)
                _tracks.value = results
                _error.value = results.isEmpty()
            } catch (e: Exception) {
                _tracks.value = emptyList()
                _error.value = true
            } finally {
                _loading.value = false
            }
        }
    }

    // Методы для работы с историей
    fun loadHistory() {
        _tracks.value = historyInteractor.getHistory()
    }

    fun addTrackToHistory(track: Track) {
        historyInteractor.addTrack(track)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _tracks.value = emptyList()
    }
}

