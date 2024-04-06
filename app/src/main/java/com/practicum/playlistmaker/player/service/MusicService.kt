package com.practicum.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
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
                setState(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        songUrl = intent?.getStringExtra(SONG_URL_TAG) ?: ""
        songDescription = intent?.getStringExtra(SONG_DESCRIPTION_TAG) ?: ""
        initMediaPlayer()

        createNotificationChannel()
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
        setState(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        timerJob?.cancel()
        setState(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    override fun playbackControl() {
        playerInteractor.playbackControl({ startPlayer() }, { pausePlayer() })
    }

    override fun showNotification() {
        ServiceCompat.startForeground(
            /* service = */ this,
            /* id = */ SERVICE_NOTIFICATION_ID,
            /* notification = */ createServiceNotification(),
            /* foregroundServiceType = */ getForegroundServiceTypeConstant()
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun initMediaPlayer() {
        setState(PlayerState.Default())
        if (songUrl.isEmpty()) return

        playerInteractor.preparePlayer(songUrl, {
            setState(PlayerState.Prepared())
        }, {
            timerJob?.cancel()
            setState(PlayerState.Prepared())
            hideNotification()
        }, {

        })
    }

    private fun releasePlayer() {
        timerJob?.cancel()
        setState(PlayerState.Default())
        playerInteractor.release()
    }

    private fun getCurrentPlayerPosition(): String {
        return playerInteractor.getCurrentPosition(DEFAULT_TIME) ?: DEFAULT_TIME
    }

    private fun setState(playerState: PlayerState) {
        _playerState.value = playerState
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            /* id= */ NOTIFICATION_CHANNEL_ID,
            /* name= */ "playlistMaker Music service",
            /* importance= */ NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(songDescription)
            .setSmallIcon(R.drawable.ic_music_service)
            .setColor(getColor(R.color.blue))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    companion object {
        const val SONG_URL_TAG = "song_url"
        const val SONG_DESCRIPTION_TAG = "song_description"
        private const val TIME_DEBOUNCE_DELAY_MILLIS = 300L
        private const val DEFAULT_TIME = "00:00"
        private const val NOTIFICATION_CHANNEL_ID = "playlistMaker_music_service_channel"
        private const val SERVICE_NOTIFICATION_ID = 100
    }
}