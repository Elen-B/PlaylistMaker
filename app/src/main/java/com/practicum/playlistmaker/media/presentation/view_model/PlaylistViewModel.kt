package com.practicum.playlistmaker.media.presentation.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.presentation.models.PlaylistScreenState
import com.practicum.playlistmaker.utils.SingleEventLiveData
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import kotlin.random.Random

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor): ViewModel() {
    private val playlist: Playlist = Playlist()

    private val stateLiveData = MutableLiveData<PlaylistScreenState>()
    fun observeState(): LiveData<PlaylistScreenState> = stateLiveData

    private val addPlaylistTrigger = SingleEventLiveData<Playlist>()
    fun getAddPlaylistTrigger(): LiveData<Playlist> = addPlaylistTrigger

    private var isClickAllowed = true
    private val onPlaylistClickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
            isClickAllowed = it
        }

    init {
        setState(PlaylistScreenState.Empty)
    }

    private fun setState(state: PlaylistScreenState) {
        stateLiveData.postValue(state)
    }

    private fun setCurrentState() {
        when {
            !playlist.name.isNullOrEmpty() -> setState(PlaylistScreenState.Filled(playlist))
            !playlist.description.isNullOrEmpty() || !playlist.filePath.isNullOrEmpty() -> {
                setState(
                    PlaylistScreenState.NotEmpty(playlist)
                )
            }

            else -> {
                setState(PlaylistScreenState.Empty)
            }
        }
    }

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist = playlist)
        }
    }

    fun onAddPlaylistClick() {
        if (clickDebounce()) {
            addPlaylistTrigger.value = playlist
        }
    }

    fun needShowDialog() : Boolean {
        val result = stateLiveData.value is PlaylistScreenState.Filled || stateLiveData.value is PlaylistScreenState.NotEmpty
        return result
    }


    fun onPlaylistNameChanged(text: String?) {
        playlist.name = text
        setCurrentState()
    }

    fun onPlaylistDescriptionChanged(text: String?) {
        playlist.description = text
        setCurrentState()
    }

    fun onPlaylistImageChanged(uri: Uri?) {
        if (uri == null) {
            playlist.filePath = null
        } else {
            playlist.filePath = "pl" + Random.nextInt() + ".jpg"
        }
        setCurrentState()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            onPlaylistClickDebounce(true)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}