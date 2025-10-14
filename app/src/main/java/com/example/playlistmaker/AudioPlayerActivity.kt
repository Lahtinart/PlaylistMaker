package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class AudioPlayerActivity : AppCompatActivity() {

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
        val playbackProgress: TextView = findViewById(R.id.playbackProgress)

        val playButton = findViewById<ImageButton>(R.id.playButton)
        val playlistButton = findViewById<ImageButton>(R.id.playlistButton)
        val favoriteButton = findViewById<ImageButton>(R.id.favoriteButton)

        val track = intent.getParcelableExtra<Track>("track") ?: return

        trackName.text = track.trackName
        artistName.text = track.artistName

        if (track.collectionName.isNullOrEmpty()) {
            albumName.visibility = View.GONE
        } else {
            albumName.visibility = View.VISIBLE
            albumName.text = track.collectionName
        }

        if (track.releaseDate.isNullOrEmpty()) {
            releaseYear.visibility = View.GONE
        } else {
            releaseYear.visibility = View.VISIBLE
            releaseYear.text = track.releaseDate.take(4)
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

        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        playButton.setOnClickListener {
            Toast.makeText(this, "Play clicked", Toast.LENGTH_SHORT).show()
        }
        favoriteButton.setOnClickListener {
            Toast.makeText(this, "Favorite clicked", Toast.LENGTH_SHORT).show()
        }
        playlistButton.setOnClickListener {
            Toast.makeText(this, "Add to playlist clicked", Toast.LENGTH_SHORT).show()
        }

        playbackProgress.text = "00:00"
    }
}
