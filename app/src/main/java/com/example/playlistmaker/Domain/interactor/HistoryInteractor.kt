package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.model.Track

class HistoryInteractor(private val storage: SearchHistoryStorage) {

    fun getHistory(): List<Track> = storage.getHistory()

    fun addTrack(track: Track) = storage.addTrack(track)

    fun clearHistory() = storage.clearHistory()
}