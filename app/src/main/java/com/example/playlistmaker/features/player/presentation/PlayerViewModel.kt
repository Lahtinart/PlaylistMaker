package com.example.playlistmaker.features.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.features.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.features.search.domain.model.Track

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _state = MutableLiveData(PlayerState())
    val state: LiveData<PlayerState> = _state

    fun play(track: Track) {
        playerInteractor.play(track)
        _state.value = _state.value?.copy(track = track, isPlaying = true)
    }

    fun pause() {
        playerInteractor.pause()
        _state.value = _state.value?.copy(isPlaying = false)
    }

    fun stop() {
        playerInteractor.stop()
        _state.value = _state.value?.copy(isPlaying = false, progress = 0, track = null)
    }

    fun updateProgress(progress: Int) {
        _state.value = _state.value?.copy(progress = progress)
    }

    // Подписка на LiveData из Interactor
    fun bindInteractor() {
        playerInteractor.isPlaying.observeForever { playing ->
            _state.value = _state.value?.copy(isPlaying = playing)
        }
        playerInteractor.progress.observeForever { prog ->
            _state.value = _state.value?.copy(progress = prog)
        }
    }
}
