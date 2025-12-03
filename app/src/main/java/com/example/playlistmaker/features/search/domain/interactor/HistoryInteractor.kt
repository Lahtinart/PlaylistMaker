package com.example.playlistmaker.features.search.domain.interactor

import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.repository.SearchHistoryRepository

class HistoryInteractor(private val repository: SearchHistoryRepository) {
    fun getHistory(): List<Track> = repository.getHistory()
    fun addTrack(track: Track) = repository.addTrack(track)
    fun clearHistory() = repository.clearHistory()
}
