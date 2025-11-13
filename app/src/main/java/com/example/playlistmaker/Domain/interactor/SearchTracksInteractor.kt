package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchTracksInteractor(private val repository: SearchRepository) {

    suspend fun execute(term: String): List<Track> {
        return repository.searchTracks(term)
    }
}