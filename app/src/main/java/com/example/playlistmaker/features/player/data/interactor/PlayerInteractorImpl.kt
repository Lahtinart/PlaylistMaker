package com.example.playlistmaker.features.player.data.interactor

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.features.player.domain.interactor.IPlayerInteractor
import com.example.playlistmaker.features.search.domain.model.Track

class PlayerInteractorImpl : IPlayerInteractor {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _isPlaying = MutableLiveData(false)
    override val isPlaying: LiveData<Boolean> = _isPlaying

    private val _progress = MutableLiveData(0)
    override val progress: LiveData<Int> = _progress

    private var currentTrack: Track? = null

    override fun play(track: Track) {
        if (currentTrack != track) {
            stop()
            currentTrack = track
            startMedia(track.previewUrl)
        } else {
            if (mediaPlayer?.isPlaying == false) startMedia(currentTrack?.previewUrl)
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    override fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        handler.removeCallbacksAndMessages(null)
        _progress.value = 0
        currentTrack = null
    }

    private fun startMedia(url: String?) {
        if (url.isNullOrEmpty()) return

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                start()
                _isPlaying.value = true
                updateProgress()
            }
            setOnCompletionListener {
                _isPlaying.value = false
                _progress.value = 0
            }
        }
    }

    private fun updateProgress() {
        mediaPlayer?.let {
            _progress.value = it.currentPosition
            if (it.isPlaying) {
                handler.postDelayed({ updateProgress() }, 500)
            }
        }
    }
}
