package com.example.playlistmaker.features.search.data.interactor

import com.example.playlistmaker.features.search.domain.interactor.ISearchTracksInteractor
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.domain.repository.SearchRepository
import java.io.IOException

class SearchTracksInteractorImpl(
    private val repository: SearchRepository
) : ISearchTracksInteractor {
    @Throws(IOException::class)
    override suspend fun execute(term: String): List<Track> {
        return repository.searchTracks(term)
    }
}
