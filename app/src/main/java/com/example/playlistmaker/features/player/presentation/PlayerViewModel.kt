package com.example.playlistmaker.features.player.presentation

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.features.search.domain.model.Track

class PlayerViewModel() : ViewModel() {

    private val _state = MutableLiveData(PlayerState())
    val state: LiveData<PlayerState> = _state


    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentTrack: Track? = null


     fun play(track: Track) {
        if (currentTrack != track) {
            // Новый трек → остановка старого и старт с начала
            stop()
            currentTrack = track
            startMedia(track.previewUrl)
        } else {
            // Тот же трек
            mediaPlayer?.let {
                if (!it.isPlaying) {
                    it.start() // просто продолжить с текущей позиции
                    updateState(isPlaying = true)
                    updateProgress()
                }
            } ?: startMedia(currentTrack?.previewUrl) // если MediaPlayer null
        }
    }

     fun pause() {
        mediaPlayer?.pause()
        updateState(isPlaying = false)
    }

     fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
        currentTrack = null
        updateState(isPlaying = false, progress = 0, track = null)
    }

    private fun startMedia(url: String?) {
        if (url.isNullOrEmpty()) return

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                start()
                updateState(isPlaying = true)
                updateProgress()
            }
            setOnCompletionListener {
                updateState(isPlaying = false, progress = 0)
            }
        }
    }

    private fun updateProgress() {
        mediaPlayer?.let {
            updateState(progress = it.currentPosition)
            if (it.isPlaying) {
                handler.postDelayed({ updateProgress() }, 500)
            }
        }
    }

    private fun updateState(
        track: Track? = currentTrack,
        isPlaying: Boolean = _state.value?.isPlaying ?: false,
        progress: Int = _state.value?.progress ?: 0
    ) {
        _state.value = PlayerState(track, isPlaying, progress)
    }
}
