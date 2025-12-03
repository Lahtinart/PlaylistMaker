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

    private val _screenState = MutableLiveData(SearchScreenState())
    val screenState: LiveData<SearchScreenState> = _screenState

    private val _openTrackEvent = MutableLiveData<Event<Track>>()
    val openTrackEvent: LiveData<Event<Track>> = _openTrackEvent

    var currentQuery: String = ""

    fun search(query: String) {
        val q = query.trim()
        currentQuery = q

        if (q.isBlank()) {
            loadHistory()
            return
        }

        _screenState.value = _screenState.value?.copy(
            isLoading = true,
            idle = false,
            noResults = false,
            networkError = null,
            tracks = emptyList(),
            showClearHistoryButton = false
        )

        viewModelScope.launch {
            try {
                val results = searchInteractor.execute(q)
                _screenState.value = _screenState.value?.copy(
                    isLoading = false,
                    idle = false,
                    tracks = results,
                    noResults = results.isEmpty(),
                    showClearHistoryButton = false
                )
            } catch (e: IOException) {
                _screenState.value = _screenState.value?.copy(
                    isLoading = false,
                    idle = false,
                    networkError = e.localizedMessage,
                    tracks = emptyList()
                )
            } catch (e: Exception) {
                _screenState.value = _screenState.value?.copy(
                    isLoading = false,
                    idle = false,
                    networkError = e.localizedMessage,
                    tracks = emptyList()
                )
            }
        }
    }

    fun loadHistory() {
        val history = historyInteractor.getHistory()
        _screenState.value = _screenState.value?.copy(
            tracks = history,
            idle = history.isEmpty(),
            showClearHistoryButton = history.isNotEmpty(),
            isLoading = false,
            noResults = false,
            networkError = null
        )
    }

    fun addTrackToHistory(track: Track) = historyInteractor.addTrack(track)

    fun clearHistory() {
        historyInteractor.clearHistory()
        _screenState.value = _screenState.value?.copy(
            tracks = emptyList(),
            idle = true,
            showClearHistoryButton = false,
            isLoading = false,
            noResults = false,
            networkError = null
        )
    }

    fun openTrack(track: Track) {
        _openTrackEvent.value = Event(track.copy())
    }
}
