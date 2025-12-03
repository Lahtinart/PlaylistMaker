package com.example.playlistmaker.features.player.presentation

import com.example.playlistmaker.features.search.domain.model.Track

data class PlayerState(
    val track: Track? = null,
    val isPlaying: Boolean = false,
    val progress: Int = 0,
    val error: String? = null
)
