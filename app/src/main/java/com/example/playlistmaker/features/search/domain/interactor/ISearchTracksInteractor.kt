package com.example.playlistmaker.features.search.domain.interactor

import com.example.playlistmaker.features.search.domain.model.Track
import java.io.IOException

interface ISearchTracksInteractor {
    @Throws(IOException::class)
    suspend fun execute(term: String): List<Track>
}
