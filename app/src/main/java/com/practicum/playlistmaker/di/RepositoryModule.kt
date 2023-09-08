package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.data.impl.MediaRepositoryImpl
import com.practicum.playlistmaker.media.data.mapper.TrackDbMapper
import com.practicum.playlistmaker.media.domain.api.MediaRepository
import com.practicum.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.impl.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get(), get())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<MediaRepository> {
        MediaRepositoryImpl(get(), get())
    }

    factory {
        TrackDbMapper()
    }
}