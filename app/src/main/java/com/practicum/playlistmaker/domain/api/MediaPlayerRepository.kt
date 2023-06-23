package com.practicum.playlistmaker.domain.api

interface MediaPlayerRepository {
    fun prepare(previewUrl: String)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(onCompletion: () -> Unit)
    fun setOnPreparedListener(onPrepared: () -> Unit)
}