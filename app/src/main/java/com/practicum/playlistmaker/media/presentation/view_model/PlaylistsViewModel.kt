package com.practicum.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.media.presentation.models.PlaylistsScreenState

class PlaylistsViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistsScreenState>()
    fun observeState(): LiveData<PlaylistsScreenState> = stateLiveData

    init {
        setState(PlaylistsScreenState.Empty)
    }

    private fun setState(state: PlaylistsScreenState) {
        stateLiveData.postValue(state)
    }
}