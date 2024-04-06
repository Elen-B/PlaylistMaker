package com.practicum.playlistmaker.player.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

internal class MusicService : Service(), AudioPlayerControl {
    private val playerInteractor: PlayerInteractor by inject()
    private var songUrl = ""
    private var songDescription = ""

    private val binder = MusicServiceBinder()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    private val playersState = _playerState.asStateFlow()
    private var timerJob: Job? = null

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (_playerState.value is PlayerState.Playing /*mediaPlayer?.isPlaying == true*/) {
                delay(TIME_DEBOUNCE_DELAY_MILLIS)
                _playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        songUrl = intent?.getStringExtra(SONG_URL_TAG) ?: ""
        songDescription = intent?.getStringExtra(SONG_DESCRIPTION_TAG) ?: ""
        initMediaPlayer()
        Log.i("playlistMaker", "onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        releasePlayer()
    }

    override fun getPlayerState(): StateFlow<PlayerState> {
        return playersState
    }

    private fun startPlayer() {
        _playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
        startTimer()
    }

    private fun pausePlayer() {
        timerJob?.cancel()
        _playerState.value = PlayerState.Paused(getCurrentPlayerPosition())
    }

    override fun playbackControl() {
        playerInteractor.playbackControl({ startPlayer() }, { pausePlayer() })
    }

    private fun initMediaPlayer() {
        setState(PlayerState.Default())
        if (songUrl.isEmpty()) return

        playerInteractor.preparePlayer(songUrl, {
            setState(PlayerState.Prepared())
        }, {
            setState(PlayerState.Prepared())
        }, {

        })
    }

    private fun releasePlayer() {
        timerJob?.cancel()
        _playerState.value = PlayerState.Default()
        playerInteractor.release()
    }

    private fun getCurrentPlayerPosition(): String {
        return playerInteractor.getCurrentPosition(DEFAULT_TIME) ?: DEFAULT_TIME
    }

    private fun setState(playerState: PlayerState) {
        _playerState.value = playerState
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    companion object {
        const val SONG_URL_TAG = "song_url"
        const val SONG_DESCRIPTION_TAG = "song_description"
        private const val TIME_DEBOUNCE_DELAY_MILLIS = 300L
        private const val DEFAULT_TIME = "00:00"
    }
}