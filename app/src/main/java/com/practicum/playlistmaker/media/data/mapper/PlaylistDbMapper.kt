package com.practicum.playlistmaker.media.data.mapper

import com.practicum.playlistmaker.media.data.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlaylistDbMapper {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            filePath = playlist.filePath,
            trackList = playlist.trackList,
            trackCount = playlist.trackCount
        )
    }
}