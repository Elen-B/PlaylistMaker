package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(translateBaseUrl)
        .build()

    private val iTunesAPIService = retrofit.create(ITunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            try {
                val resp = iTunesAPIService.search(dto.expression).execute()
                val body = resp.body() ?: Response()

                body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                Response().apply { resultCode = 400 }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }

    companion object {
        private const val translateBaseUrl = "http://itunes.apple.com"
    }
}