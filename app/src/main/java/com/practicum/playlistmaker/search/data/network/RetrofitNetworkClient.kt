package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest

class RetrofitNetworkClient(private val iTunesAPIService: ITunesApi) : NetworkClient {
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
}