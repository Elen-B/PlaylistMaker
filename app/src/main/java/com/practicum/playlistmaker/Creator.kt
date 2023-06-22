package com.practicum.playlistmaker

import com.practicum.playlistmaker.data.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.TrackInteractor
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    fun providePlayerInteractorImpl(): PlayerInteractor {
        return PlayerInteractorImpl(MediaPlayerRepositoryImpl())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(
            TrackRepositoryImpl(RetrofitNetworkClient())
        )
    }
}