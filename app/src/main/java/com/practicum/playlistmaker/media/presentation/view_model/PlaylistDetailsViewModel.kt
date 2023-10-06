package com.practicum.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.LocalStorageInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.presentation.models.PlaylistDetailsScreenState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.getMinuteCountNoun
import com.practicum.playlistmaker.utils.getTrackCountNoun
import kotlinx.coroutines.launch
import java.io.File

class PlaylistDetailsViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor,
    private val localStorageInteractor: LocalStorageInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistDetailsScreenState>()
    fun observeState(): LiveData<PlaylistDetailsScreenState> = stateLiveData

    private val trackListLiveData = MutableLiveData<List<Track>>()
    fun observeTrackList(): LiveData<List<Track>> = trackListLiveData

    init {
        loadContent()
    }

    private fun loadContent() {
        setState(PlaylistDetailsScreenState.Loading)

        viewModelScope.launch {
            playlistInteractor
                .getFlowPlaylistById(playlistId)
                .collect { playlist ->
                    processResult(playlist)
                    setTrackList(playlistInteractor.getPlaylistTracksByTrackIdList(playlist.trackList))
                }
        }
    }

    private fun setState(state: PlaylistDetailsScreenState) {
        stateLiveData.value = state
    }

    private fun setTrackList(trackList: List<Track>) {
        trackListLiveData.value = trackList
    }

    private fun processResult(playlist: Playlist) {
        setState(PlaylistDetailsScreenState.Content(playlist))
    }

    private fun getTrackCount(): Int = trackListLiveData.value?.size ?: 0

    private fun getPlaylistTimeMillis(): Long = trackListLiveData.value?.sumOf { it.trackTimeMillis ?: 0 } ?: 0

    fun getImageDirectory(): File = localStorageInteractor.getImageDirectory()

    fun getTrackCountStatistics(): String {
        val trackCount = getTrackCount()
        return trackCount.toString() + " " + getTrackCountNoun(trackCount)
    }

    fun getPlaylistTimeStatistics(): String {
        val minutes = getPlaylistTimeMillis() / 1000 / 60
        return minutes.toString() + " " + getMinuteCountNoun(minutes)
    }
}