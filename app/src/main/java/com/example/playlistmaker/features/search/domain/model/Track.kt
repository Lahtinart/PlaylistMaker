package com.example.playlistmaker.features.search.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val previewUrl: String? = null
) : Parcelable {

    fun getCoverArtwork(): String =
        artworkUrl100.replaceAfterLast("/", "512x512bb.jpg")
}
