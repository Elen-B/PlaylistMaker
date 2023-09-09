package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.api.MediaInteractor
import com.practicum.playlistmaker.media.domain.api.MediaRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class MediaInteractorImpl(private val mediaRepository: MediaRepository): MediaInteractor {
    override fun getFavouriteTracks(): Flow<List<Track>> {
        return mediaRepository.getFavouriteTracks()
    }

    override suspend fun saveFavouriteTrack(track: Track) {
        mediaRepository.saveFavouriteTrack(track)
    }

    override suspend fun deleteFavouriteTrack(trackId: Long) {
        mediaRepository.deleteFavouriteTrack(trackId)
    }

    override suspend fun getFavouriteState(trackId: Long): Boolean {
        return mediaRepository.getFavouriteState(trackId)
    }
}