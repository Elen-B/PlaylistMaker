package com.practicum.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.FavouritesInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenMode
import com.practicum.playlistmaker.player.presentation.models.TrackAddProcessStatus
import com.practicum.playlistmaker.player.service.AudioPlayerControl
import com.practicum.playlistmaker.player.service.PlayerState
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.SingleEventLiveData
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val favouritesInteractor: FavouritesInteractor,
    private val historyInteractor: HistoryInteractor,
    private val playlistInteractor: PlaylistInteractor,
) :
    ViewModel() {

    private val modeLiveData = MutableLiveData<PlayerScreenMode>()
    fun observeMode(): LiveData<PlayerScreenMode> = modeLiveData

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val favouriteLiveData = MutableLiveData<Boolean>()
    fun observeFavourite(): LiveData<Boolean> = favouriteLiveData

    private val trackAddProcessStatus = SingleEventLiveData<TrackAddProcessStatus>()
    fun getAddProcessStatus(): LiveData<TrackAddProcessStatus> = trackAddProcessStatus

    private var audioPlayerControl: AudioPlayerControl? = null
    private var playlists: List<Playlist> = listOf()

    init {
        viewModelScope.launch {
            track.isFavourite = favouritesInteractor.getFavouriteState(track.trackId ?: 0)
            setFavourite(track.isFavourite)
        }

        viewModelScope.launch {
            playlistInteractor
                .getFlowPlaylists()
                .collect {
                    playlists = it
                }
        }
        setMode(PlayerScreenMode.Player)
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.getPlayerState().collect {
                setState(it)
            }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    private fun setState(state: PlayerState) {
        stateLiveData.value = state
    }

    private fun setFavourite(isFavourite: Boolean) {
        favouriteLiveData.value = isFavourite
    }

    private fun setMode(mode: PlayerScreenMode) {
        modeLiveData.value = mode
    }

    private fun setAddProcessStatus(status: TrackAddProcessStatus) {
        trackAddProcessStatus.value = status
    }

    fun playBackControl() {
        audioPlayerControl?.playbackControl()
    }

    private fun saveFavouriteTrack() {
        viewModelScope.launch {
            favouritesInteractor.saveFavouriteTrack(track)
        }
    }

    private fun deleteFavouriteTrack() {
        viewModelScope.launch {
            favouritesInteractor.deleteFavouriteTrack(track.trackId!!)
        }
    }

    fun onLikeTrackClick() {
        track.isFavourite = !track.isFavourite
        setFavourite(track.isFavourite)
        if (track.isFavourite) {
            saveFavouriteTrack()
        } else {
            deleteFavouriteTrack()
        }
        viewModelScope.launch{
            historyInteractor.addTrackToSearchHistory(track)
        }
    }

    fun onNewPlaylistClick() {
        setMode(PlayerScreenMode.NewPlaylist)
    }

    fun onCancelBottomSheet() {
        setMode(PlayerScreenMode.Player)
    }

    fun onPlayerAddTrackClick() {
        setMode(PlayerScreenMode.BottomSheet(playlists))
    }

    fun addTrackToPlaylist(playlistId: Long, playlistName: String?) {
        viewModelScope.launch {
            try {
                playlistInteractor.addTrackToPlaylist(track, playlistId)
                setAddProcessStatus(TrackAddProcessStatus.Added(playlistName))
            } catch (e: Exception) {
                setAddProcessStatus(TrackAddProcessStatus.Error(playlistName))
            }
        }
        setMode(PlayerScreenMode.Player)
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        if (playlist.trackList.indexOf(track.trackId) == -1) {
            addTrackToPlaylist(playlist.id, playlist.name)
        } else {
            setAddProcessStatus(TrackAddProcessStatus.Exist(playlist.name))
        }
    }

    fun showNotification() {
        if (stateLiveData.value is PlayerState.Playing) {
            audioPlayerControl?.showNotification()
        }
    }

    fun hideNotification() {
        audioPlayerControl?.hideNotification()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerControl = null
    }
}