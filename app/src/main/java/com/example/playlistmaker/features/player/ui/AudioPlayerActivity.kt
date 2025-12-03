package com.example.playlistmaker.features.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.features.player.presentation.PlayerViewModel
import com.example.playlistmaker.features.player.presentation.PlayerViewModelFactory
import com.example.playlistmaker.features.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.features.search.domain.model.Track

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var playbackProgress: TextView

    private val playerInteractor = PlayerInteractor()
    private val playerViewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(playerInteractor)
    }

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

        // UI
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
            playerViewModel.stop()
            onBackPressedDispatcher.onBackPressed()
        }

        playButton.setOnClickListener {
            val url = track.previewUrl
            if (url.isNullOrEmpty()) {
                Toast.makeText(this, "Preview not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (playerViewModel.isPlaying.value == true) {
                playerViewModel.pause() // если играет — пауза
            } else {
                playerViewModel.play(track) // если не играет — запускаем
            }
        }

        favoriteButton.setOnClickListener {
            Toast.makeText(this, "Favorite clicked", Toast.LENGTH_SHORT).show()
        }

        playlistButton.setOnClickListener {
            Toast.makeText(this, "Add to playlist clicked", Toast.LENGTH_SHORT).show()
        }

        observePlayer()
    }

    private fun observePlayer() {
        playerViewModel.isPlaying.observe(this, Observer { isPlaying ->
            playButton.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        })

        playerViewModel.progress.observe(this, Observer { progress ->
            val minutes = progress / 1000 / 60
            val seconds = progress / 1000 % 60
            playbackProgress.text = String.format("%02d:%02d", minutes, seconds)
        })
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.stop()
    }
}
