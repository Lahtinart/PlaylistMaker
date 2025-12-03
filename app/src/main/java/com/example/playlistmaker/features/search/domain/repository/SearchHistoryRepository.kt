package com.example.playlistmaker.features.search.domain.repository

import com.example.playlistmaker.features.search.domain.model.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}
