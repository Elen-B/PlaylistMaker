package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.domain.api.FavouritesInteractor
import com.practicum.playlistmaker.media.domain.impl.FavouritesInteractorImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }
}