package com.example.playlistmaker.features.search.presentation

import com.example.playlistmaker.features.search.domain.model.Track

data class SearchScreenState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val showClearHistoryButton: Boolean = false,
    val noResults: Boolean = false,
    val networkError: String? = null,
    val idle: Boolean = true
)
