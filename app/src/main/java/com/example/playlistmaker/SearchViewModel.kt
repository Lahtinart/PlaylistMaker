package com.example.playlistmaker

import ITunesApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(private val api: ITunesApi) : ViewModel() {

    var searchText: String = ""

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun performSearch(query: String) {
        if (query.isBlank()) return

        _loading.value = true
        _error.value = false

        api.searchTracks(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                _loading.value = false

                val results = response.body()?.results?.map { apiTrack ->
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
                        previewUrl = apiTrack.previewUrl // теперь previewUrl будет заполнен
                    )
                } ?: emptyList()
                _tracks.value = results

                _tracks.value = results
                _error.value = !response.isSuccessful
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                _loading.value = false
                _tracks.value = emptyList()
                _error.value = true
            }
        })
    }
}