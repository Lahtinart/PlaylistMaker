package com.example.playlistmaker.features.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.features.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.features.search.domain.model.Track

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    val isPlaying: LiveData<Boolean> = playerInteractor.isPlaying
    val progress: LiveData<Int> = playerInteractor.progress

    fun play(track: Track) = playerInteractor.play(track)
    fun pause() = playerInteractor.pause()
    fun stop() = playerInteractor.stop()
}