package com.example.playlistmaker.features.search.data.interactor

import com.example.playlistmaker.features.search.domain.interactor.IHistoryInteractor
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.repository.SearchHistoryRepository

class HistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : IHistoryInteractor {
    override fun getHistory(): List<Track> = repository.getHistory()
    override fun addTrack(track: Track) = repository.addTrack(track)
    override fun clearHistory() = repository.clearHistory()
}
