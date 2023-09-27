package com.practicum.playlistmaker.media.domain.api

import com.practicum.playlistmaker.media.domain.models.Playlist

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)
}