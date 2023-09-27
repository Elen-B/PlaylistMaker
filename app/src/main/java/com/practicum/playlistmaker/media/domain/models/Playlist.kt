package com.practicum.playlistmaker.media.domain.models

data class Playlist(
    val id: Int? = null,
    var name: String? = null,
    var description: String? = null,
    var filePath: String? = null,
    var trackList: String? = null,
    var trackCount: Int = 0,
)
