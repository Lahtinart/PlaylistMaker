package com.example.playlistmaker

import ITunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofit = Retrofit.Builder()
    .baseUrl("https://itunes.apple.com")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api: ITunesApi = retrofit.create(ITunesApi::class.java)