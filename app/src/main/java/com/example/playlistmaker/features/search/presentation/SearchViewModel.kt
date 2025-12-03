package com.example.playlistmaker.features.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.util.Event
import com.example.playlistmaker.features.search.domain.interactor.HistoryInteractor
import com.example.playlistmaker.features.search.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.features.search.domain.model.Track
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val searchInteractor: SearchTracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>(SearchState.Idle)
    val state: LiveData<SearchState> = _state

    private val _openTrackEvent = MutableLiveData<Event<Track>>()
    val openTrackEvent: LiveData<Event<Track>> = _openTrackEvent

    private val _showClearHistoryButton = MutableLiveData(false)
    val showClearHistoryButton: LiveData<Boolean> = _showClearHistoryButton

    var currentQuery: String = ""

    fun search(query: String) {
        val q = query.trim()
        currentQuery = q

        if (q.isBlank()) {
            loadHistory()
            return
        }

        _state.value = SearchState.Loading
        _showClearHistoryButton.value = false   // всегда скрываем при поиске/ошибках

        viewModelScope.launch {
            try {
                val results = searchInteractor.execute(q)

                _state.value =
                    if (results.isEmpty()) SearchState.NoResults
                    else SearchState.Content(results)

            } catch (e: IOException) {
                _state.value = SearchState.NetworkError(e.localizedMessage)
            } catch (e: Exception) {
                _state.value = SearchState.NetworkError(e.localizedMessage)
            }
        }
    }

    fun loadHistory() {
        val history = historyInteractor.getHistory()

        if (history.isEmpty()) {
            _state.value = SearchState.Idle
            _showClearHistoryButton.value = false
        } else {
            _state.value = SearchState.Content(history)
            _showClearHistoryButton.value = true
        }
    }

    fun addTrackToHistory(track: Track) = historyInteractor.addTrack(track)

    fun clearHistory() {
        historyInteractor.clearHistory()
        _state.value = SearchState.Idle
        _showClearHistoryButton.value = false
    }

    fun openTrack(track: Track) {
        _openTrackEvent.value = Event(track.copy())
    }
}

