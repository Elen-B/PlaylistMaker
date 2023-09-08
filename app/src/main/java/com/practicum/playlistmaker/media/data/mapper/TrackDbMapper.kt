package com.practicum.playlistmaker.media.data.mapper

import com.practicum.playlistmaker.media.data.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

class TrackDbMapper {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime= track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.albumName,
            releaseYear = track.releaseYear,
            primaryGenreName = track.genreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTime,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseYear,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl,
            isFavourite = true
        )
    }
}