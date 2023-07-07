package com.practicum.playlistmaker.player.presentation.view_model

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerViewModel(private val track: Track, private val playerInteractor: PlayerInteractor) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerScreenState>()
    private val handler = Handler(Looper.getMainLooper())

    init {
        loadPlayer()
    }
    fun observeState(): LiveData<PlayerScreenState> = stateLiveData

    private fun loadPlayer() {
        renderState(PlayerScreenState.Default(track))
        playerInteractor.preparePlayer(track.previewUrl, {
            renderState(PlayerScreenState.Prepared)
        }, {
            renderState(PlayerScreenState.Paused(DEFAULT_TIME))
        }, {

        })
    }
    private fun renderState(state: PlayerScreenState) {
        stateLiveData.postValue(state)
    }
    private fun runTimerTask(): Runnable {
        return Runnable {
            renderState(PlayerScreenState.Progress(getCurrentTime()))
            handler.postDelayed(runTimerTask(), TIME_DEBOUNCE_DELAY)
        }
    }

    private fun onStartPlayer() {
        renderState(PlayerScreenState.Playing())
        handler.post(runTimerTask())
    }

    private fun onPausePlayer() {
        stopTimerTask()
        renderState(PlayerScreenState.Paused())
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
        fun factory(track: Track): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    Log.e("render", "initializer")
                    PlayerViewModel(track, Creator.providePlayerInteractorImpl())
                }
            }
        }
    }
}