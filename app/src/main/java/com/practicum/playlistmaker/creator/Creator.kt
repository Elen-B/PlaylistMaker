package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }
    fun providePlayerInteractorImpl(): PlayerInteractor {
        return PlayerInteractorImpl(MediaPlayerRepositoryImpl())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(
            TrackRepositoryImpl(RetrofitNetworkClient())
        )
    }

    fun provideSharingInteractor(context: Context) : SharingInteractor {
        return  SharingInteractorImpl(getExternalNavigator(context))
    }
}