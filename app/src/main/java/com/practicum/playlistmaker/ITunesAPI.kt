package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String) : Call<TrackSearchResponse>

    companion object Factory {
        private const val translateBaseUrl = "http://itunes.apple.com" // "https://itunes.apple.com" не работает сейчас

        fun create(): ITunesAPI {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(translateBaseUrl)
                .build()
            return retrofit.create(ITunesAPI::class.java)
        }
    }
}