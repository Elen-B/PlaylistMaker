package com.practicum.playlistmaker.player.service

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getPlayerState(): StateFlow<PlayerState>
    fun playbackControl()
    fun showNotification()
    fun hideNotification()
}