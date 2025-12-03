package com.example.playlistmaker.features.search.data.dto

// Класс для Retrofit, чтобы корректно принимать JSON с API
data class SearchResponse(
    val resultCount: Int,
    val results: List<ApiTrack>
)

// "Сырой" трек из API
data class ApiTrack(
    val trackId: String?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: String?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)

