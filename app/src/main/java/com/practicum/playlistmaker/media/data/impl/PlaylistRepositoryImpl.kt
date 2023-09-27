package com.practicum.playlistmaker.media.data.impl

import com.practicum.playlistmaker.media.data.AppDatabase
import com.practicum.playlistmaker.media.data.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.mapper.PlaylistDbMapper
import com.practicum.playlistmaker.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbMapper: PlaylistDbMapper
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbMapper.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(mapFromPlaylistEntity(playlists))
    }

    private fun mapFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbMapper.map(playlist) }
    }
}