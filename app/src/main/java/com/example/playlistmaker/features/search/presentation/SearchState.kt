package com.example.playlistmaker.features.search.presentation

import com.example.playlistmaker.features.search.domain.model.Track

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    object NoResults : SearchState()
    data class NetworkError(val message: String? = null) : SearchState()
}

