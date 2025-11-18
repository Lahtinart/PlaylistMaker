package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class AudioPlayerActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val STATE_PREPARING = 4
    }

    private var playerState = STATE_DEFAULT

    private lateinit var playButton: ImageButton
    private lateinit var playbackProgress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val backButton: ImageButton = findViewById(R.id.backButton)
        val coverImage: ImageView = findViewById(R.id.coverImage)
        val trackName: TextView = findViewById(R.id.trackName)
        val artistName: TextView = findViewById(R.id.artistName)
        val albumName: TextView = findViewById(R.id.albumName)
        val releaseYear: TextView = findViewById(R.id.releaseYear)
        val genre: TextView = findViewById(R.id.genre)
        val country: TextView = findViewById(R.id.country)
        val duration: TextView = findViewById(R.id.duration)
        playbackProgress = findViewById(R.id.playbackProgress)

        playButton = findViewById(R.id.playButton)
        val playlistButton = findViewById<ImageButton>(R.id.playlistButton)
        val favoriteButton = findViewById<ImageButton>(R.id.favoriteButton)

        val track = intent.getParcelableExtra<Track>("track") ?: run {
            Toast.makeText(this, "Track not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Заполнение UI
        trackName.text = track.trackName
        artistName.text = track.artistName
        if (track.collectionName.isNullOrEmpty()) albumName.visibility = View.GONE else albumName.apply {
            visibility = View.VISIBLE
            text = track.collectionName
        }
        if (track.releaseDate.isNullOrEmpty()) releaseYear.visibility = View.GONE else releaseYear.apply {
            visibility = View.VISIBLE
            text = track.releaseDate.take(4)
        }
        genre.text = track.primaryGenreName ?: ""
        country.text = track.country ?: ""
        val totalMillis = track.trackTimeMillis.toLongOrNull() ?: 0L
        val minutes = totalMillis / 1000 / 60
        val seconds = totalMillis / 1000 % 60
        duration.text = String.format("%d:%02d", minutes, seconds)

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_cover)
            .error(R.drawable.placeholder_cover)
            .into(coverImage)

        playbackProgress.text = "00:00"

        backButton.setOnClickListener {
            stopPlayback()
            onBackPressedDispatcher.onBackPressed()
        }

        playButton.setOnClickListener {
            val url = track.previewUrl
            if (url.isNullOrEmpty()) {
                Toast.makeText(this, "Preview not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            when (playerState) {
                STATE_PLAYING -> pausePlayback()
                STATE_DEFAULT, STATE_PREPARED, STATE_PAUSED -> startPlayback(url)
                STATE_PREPARING -> {} // игнорируем клик
            }
        }

        favoriteButton.setOnClickListener {
            Toast.makeText(this, "Favorite clicked", Toast.LENGTH_SHORT).show()
        }

        playlistButton.setOnClickListener {
            Toast.makeText(this, "Add to playlist clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startPlayback(url: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            try {
                mediaPlayer?.setDataSource(url)
                mediaPlayer?.prepareAsync()
                playerState = STATE_PREPARING
                playButton.isEnabled = false
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Cannot play track", Toast.LENGTH_SHORT).show()
                return
            }

            mediaPlayer?.setOnPreparedListener {
                playerState = STATE_PREPARED
                playButton.isEnabled = true
                startMedia()
            }

            mediaPlayer?.setOnCompletionListener {
                playerState = STATE_PREPARED
                playButton.setImageResource(R.drawable.ic_play)
                playbackProgress.text = "00:00"
            }
        } else {
            startMedia()
        }
    }

    private fun startMedia() {
        mediaPlayer?.let {
            if (!it.isPlaying) it.start()
            playButton.setImageResource(R.drawable.ic_pause) // переключаем на паузу
            playerState = STATE_PLAYING
            updateProgress()
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        playButton.setImageResource(R.drawable.ic_play) // переключаем на плей
        playerState = STATE_PAUSED
    }

    private fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
        playerState = STATE_DEFAULT
    }

    private fun updateProgress() {
        mediaPlayer?.let {
            val minutes = it.currentPosition / 1000 / 60
            val seconds = it.currentPosition / 1000 % 60
            playbackProgress.text = String.format("%02d:%02d", minutes, seconds)

            if (playerState == STATE_PLAYING) {
                handler.postDelayed({ updateProgress() }, 500)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
    }
}
