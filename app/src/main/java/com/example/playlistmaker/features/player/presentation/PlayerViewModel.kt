package com.example.playlistmaker.features.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.features.player.domain.interactor.IPlayerInteractor
import com.example.playlistmaker.features.search.domain.model.Track

class PlayerViewModel(private val playerInteractor: IPlayerInteractor) : ViewModel() {

    private val _state = MutableLiveData(PlayerState())
    val state: LiveData<PlayerState> = _state

    init {
        // initial sync if values already exist
        _state.value = _state.value?.copy(
            isPlaying = playerInteractor.isPlaying.value ?: false,
            progress = playerInteractor.progress.value ?: 0
        )
    }

    fun play(track: Track) {
        // обновляем стейт немедленно, чтобы UI сразу поменял плейсхолдер
        _state.value = _state.value?.copy(track = track, isPlaying = true)
        playerInteractor.play(track)
    }

    fun pause() {
        _state.value = _state.value?.copy(isPlaying = false)
        playerInteractor.pause()
    }

    fun stop() {
        _state.value = _state.value?.copy(isPlaying = false, progress = 0, track = null)
        playerInteractor.stop()
    }

    fun bindInteractor() {
        playerInteractor.isPlaying.observeForever { playing ->
            _state.value = _state.value?.copy(isPlaying = playing)
        }
        playerInteractor.progress.observeForever { prog ->
            _state.value = _state.value?.copy(progress = prog)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // For this simplified version we didn't track observers; production code should remove observers here.
    }
}
