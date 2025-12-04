package com.example.playlistmaker.features.player.domain.interactor

import androidx.lifecycle.LiveData
import com.example.playlistmaker.features.search.domain.model.Track

interface IPlayerInteractor {
    val isPlaying: LiveData<Boolean>
    val progress: LiveData<Int>

    fun play(track: Track)
    fun pause()
    fun stop()
}
