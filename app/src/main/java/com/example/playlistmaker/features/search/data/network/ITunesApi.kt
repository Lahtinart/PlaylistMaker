package com.example.playlistmaker.features.search.data.network

import com.example.playlistmaker.features.search.data.dto.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {

    @GET("/search?entity=song")
    suspend fun searchTracks(
        @Query("term") text: String
    ): SearchResponse
}

