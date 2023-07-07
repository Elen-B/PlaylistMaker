package com.practicum.playlistmaker.player.presentation.mapper

import com.practicum.playlistmaker.player.presentation.models.ParcelableTrack
import com.practicum.playlistmaker.search.domain.models.Track

object ParcelableTrackMapper {

    fun map(track: Track): ParcelableTrack {
        return ParcelableTrack(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            albumName = track.albumName,
            releaseDate = track.releaseDate,
            genreName = track.genreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun map(track: ParcelableTrack): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            albumName = track.albumName,
            releaseDate = track.releaseDate,
            genreName = track.genreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}