package com.practicum.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.MediaInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val mediaInteractor: MediaInteractor,
    private val historyInteractor: HistoryInteractor,
) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerScreenState>()
    fun observeState(): LiveData<PlayerScreenState> = stateLiveData

    private val favouriteLiveData = MutableLiveData<Boolean>()
    fun observeFavourite(): LiveData<Boolean> = favouriteLiveData

    private var currentTime: String? = null
    private var timerJob: Job? = null

    init {
        loadPlayer()
        setFavourite(track.isFavourite)
    }

    private fun loadPlayer() {
        setState(PlayerScreenState.Default(track))
        playerInteractor.preparePlayer(track.previewUrl, {
            setState(PlayerScreenState.Prepared)
        }, {
            setState(PlayerScreenState.Paused(DEFAULT_TIME))
        }, {

        })
    }

    private fun setState(state: PlayerScreenState) {
        stateLiveData.value = state
    }

    private fun setFavourite(isFavourite: Boolean) {
        favouriteLiveData.value = isFavourite
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (stateLiveData.value is PlayerScreenState.Playing || stateLiveData.value is PlayerScreenState.Progress) {
                currentTime = getCurrentTime()
                setState(PlayerScreenState.Progress(getCurrentTime()))
                delay(TIME_DEBOUNCE_DELAY)
            }
        }
    }

    private fun onStartPlayer() {
        setState(PlayerScreenState.Playing())
        startTimer()
    }

    private fun onPausePlayer() {
        setState(PlayerScreenState.Paused(currentTime))
        timerJob?.cancel()
    }

    private fun getCurrentTime(): String? = playerInteractor.getCurrentPosition(DEFAULT_TIME)

    fun playBackControl() {
        playerInteractor.playbackControl({ onStartPlayer() }, { onPausePlayer() })
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer { onPausePlayer() }
    }

    private fun saveFavouriteTrack() {
        viewModelScope.launch {
            mediaInteractor.saveFavouriteTrack(track)
        }
    }

    private fun deleteFavouriteTrack() {
        viewModelScope.launch {
            mediaInteractor.deleteFavouriteTrack(track.trackId!!)
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

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    companion object {
        private const val DEFAULT_TIME = "00:00"
        private const val TIME_DEBOUNCE_DELAY = 300L
    }
}