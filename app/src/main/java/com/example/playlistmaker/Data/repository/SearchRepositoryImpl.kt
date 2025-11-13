package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchRepositoryImpl : SearchRepository {
    override suspend fun searchTracks(term: String): List<Track> {
        val response = NetworkClient.api.searchTracks(term).execute() // синхронный вызов для примера
        return response.body()?.results?.map { apiTrack ->
            Track(
                trackId = apiTrack.trackId ?: "0",
                trackName = apiTrack.trackName ?: "Unknown",
                artistName = apiTrack.artistName ?: "Unknown",
                trackTimeMillis = apiTrack.trackTimeMillis ?: "0",
                artworkUrl100 = apiTrack.artworkUrl100 ?: "",
                collectionName = apiTrack.collectionName,
                releaseDate = apiTrack.releaseDate,
                primaryGenreName = apiTrack.primaryGenreName,
                country = apiTrack.country,
                previewUrl = apiTrack.previewUrl
            )
        } ?: emptyList()
    }
}