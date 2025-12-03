package com.example.playlistmaker.features.search.domain.repository

import com.example.playlistmaker.features.search.domain.model.Track

interface SearchRepository {
    suspend fun searchTracks(term: String): List<Track>
}