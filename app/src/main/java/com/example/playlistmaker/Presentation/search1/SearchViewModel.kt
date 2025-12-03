package com.example.playlistmaker.presentation.search1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val searchInteractor: SearchTracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    var searchText: String = ""

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<Boolean>() // сеть
    val error: LiveData<Boolean> = _error

    private val _noResults = MutableLiveData<Boolean>() // пустой результат
    val noResults: LiveData<Boolean> = _noResults

    fun search() {
        if (searchText.isBlank()) return

        _loading.value = true
        _error.value = false
        _noResults.value = false

        viewModelScope.launch {
            try {
                val results = searchInteractor.execute(searchText)
                _tracks.value = results
                _noResults.value = results.isEmpty() // только если запрос успешен
            } catch (e: IOException) {
                _tracks.value = emptyList()
                _error.value = true // сеть отсутствует
                _noResults.value = false // не показываем "нет результатов"
            } catch (e: Exception) {
                _tracks.value = emptyList()
                _error.value = true
                _noResults.value = false
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadHistory() {
        _tracks.value = historyInteractor.getHistory()
        _error.value = false
        _noResults.value = false
    }

    fun addTrackToHistory(track: Track) {
        historyInteractor.addTrack(track)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _tracks.value = emptyList()
        _error.value = false
        _noResults.value = false
    }
}
