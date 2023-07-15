package com.practicum.playlistmaker.player.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerViewModel(private val track: Track, private val playerInteractor: PlayerInteractor) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerScreenState>()
    private val handler = Handler(Looper.getMainLooper())
    private var currentTime: String? = null

    init {
        loadPlayer()
    }
    fun observeState(): LiveData<PlayerScreenState> = stateLiveData

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
        stateLiveData.postValue(state)
    }

    private fun runTimerTask(): Runnable {
        return Runnable {
            currentTime = getCurrentTime()
            if (stateLiveData.value is PlayerScreenState.Playing || stateLiveData.value is PlayerScreenState.Progress) {
                handler.postDelayed(runTimerTask(), TIME_DEBOUNCE_DELAY)
                setState(PlayerScreenState.Progress(getCurrentTime()))
            }
        }
    }

    private fun onStartPlayer() {
        setState(PlayerScreenState.Playing())
        handler.post(runTimerTask())
    }

    private fun onPausePlayer() {
        setState(PlayerScreenState.Paused(currentTime))
        stopTimerTask()
    }

    private fun getCurrentTime() : String? = playerInteractor.getCurrentPosition(DEFAULT_TIME)

    fun playBackControl() {
        playerInteractor.playbackControl({ onStartPlayer() }, { onPausePlayer() })
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer { onPausePlayer() }
    }

    private fun stopTimerTask() {
        handler.removeCallbacks(runTimerTask())
    }

    override fun onCleared() {
        super.onCleared()
        stopTimerTask()
        playerInteractor.release()
    }

    companion object {
        private const val DEFAULT_TIME = "00:00"
        private const val TIME_DEBOUNCE_DELAY = 500L
    }
}