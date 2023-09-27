package com.practicum.playlistmaker.media.presentation.view_model

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.presentation.models.PlaylistsScreenState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel(), DefaultLifecycleObserver {

    private val stateLiveData = MutableLiveData<PlaylistsScreenState>()
    fun observeState(): LiveData<PlaylistsScreenState> = stateLiveData

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        loadContent()
    }

    private fun loadContent() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            setState(PlaylistsScreenState.Empty)
        } else {
            setState(PlaylistsScreenState.Content(playlists))
        }
    }

    private fun setState(state: PlaylistsScreenState) {
        stateLiveData.postValue(state)
    }
}