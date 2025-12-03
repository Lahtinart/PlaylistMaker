package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchRepository
import java.io.IOException

class SearchTracksInteractor(private val repository: SearchRepository) {

    @Throws(IOException::class) // чтобы было понятно, что может быть IOException
    suspend fun execute(term: String): List<Track> {
        return repository.searchTracks(term)
    }
}