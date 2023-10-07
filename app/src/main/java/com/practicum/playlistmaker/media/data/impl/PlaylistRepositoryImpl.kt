package com.practicum.playlistmaker.media.data.impl

import com.practicum.playlistmaker.media.data.AppDatabase
import com.practicum.playlistmaker.media.data.mapper.PlaylistDbMapper
import com.practicum.playlistmaker.media.data.mapper.PlaylistTrackDbMapper
import com.practicum.playlistmaker.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Exception

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbMapper: PlaylistDbMapper,
    private val playlistTrackDbMapper: PlaylistTrackDbMapper
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        val playlistEntity = playlistDbMapper.map(playlist)
        return try {
            val result = appDatabase.playlistDao().insertPlaylist(playlistEntity)
            result
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist {
        return playlistDbMapper.map(appDatabase.playlistDao().getPlaylistById(id))
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return appDatabase.playlistDao().getPlaylists().map { playlistEntity -> playlistDbMapper.map(playlistEntity) }
    }

    override suspend fun getFlowPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getFlowPlaylists().map { it.map { playlistEntity -> playlistDbMapper.map(playlistEntity) } }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        val playlist = getPlaylistById(playlistId)
        appDatabase.playlistTrackDao().insertPlaylistTrack(playlistTrackDbMapper.map(track))
        playlist.trackList.add(track.trackId!!)
        playlist.trackCount +=1
        appDatabase.playlistDao().updatePlaylist(playlistDbMapper.map(playlist))
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Long) {
        val playlist = getPlaylistById(playlistId)
        playlist.trackList.remove(track.trackId)
        playlist.trackCount -= 1
        appDatabase.playlistDao().updatePlaylist(playlistDbMapper.map(playlist))
        if (isUnusedPlaylistTrack(track)) {
            appDatabase.playlistTrackDao().deletePlaylistTrack(playlistTrackDbMapper.map(track))
        }
    }

    override suspend fun getFlowPlaylistById(id: Long): Flow<Playlist> {
        return appDatabase.playlistDao().getFlowPlaylistById(id).map { playlistEntity -> playlistDbMapper.map(playlistEntity) }
    }

    override suspend fun getPlaylistTracks(): List<Track> {
        return appDatabase.playlistTrackDao().getPlaylistTracks().map { playlistTrackEntity -> playlistTrackDbMapper.map(playlistTrackEntity)}
    }

    override suspend fun getPlaylistTracksByTrackIdList(trackIdList: List<Long>): List<Track> {
        return if (trackIdList.isEmpty())
            listOf()
        else {
            val allPlaylistTracks = getPlaylistTracks()
            allPlaylistTracks.filter { trackIdList.indexOf(it.trackId) > -1 }
        }
    }

    private suspend fun isUnusedPlaylistTrack(track: Track): Boolean {
        val playlists = getPlaylists().filter { playlist -> playlist.trackList.indexOf(track.trackId) > -1 }
        return playlists.isEmpty()
    }
}