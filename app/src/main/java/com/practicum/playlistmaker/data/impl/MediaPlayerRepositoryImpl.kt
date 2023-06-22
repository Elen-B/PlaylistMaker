package com.practicum.playlistmaker.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl: MediaPlayerRepository {
    private val mediaPlayer = MediaPlayer()

    override fun prepare(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition

    override fun setOnCompletionListener(onCompletion: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            onCompletion()
        }
    }

    override fun setOnPreparedListener(onPrepared: () -> Unit) {
        mediaPlayer.setOnPreparedListener {
            onPrepared()
        }
    }
}