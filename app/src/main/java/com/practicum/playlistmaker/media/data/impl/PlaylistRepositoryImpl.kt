package com.practicum.playlistmaker.media.data.impl

import com.practicum.playlistmaker.media.data.AppDatabase
import com.practicum.playlistmaker.media.data.mapper.PlaylistDbMapper
import com.practicum.playlistmaker.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbMapper: PlaylistDbMapper
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbMapper.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }
}