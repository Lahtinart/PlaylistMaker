package com.example.playlistmaker.features.search.domain.interactor

import com.example.playlistmaker.features.search.domain.model.Track

interface IHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}
