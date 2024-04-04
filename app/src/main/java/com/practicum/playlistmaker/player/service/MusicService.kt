package com.practicum.playlistmaker.player.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

internal class MusicService : Service() {
    private var songUrl = ""
    private var songDescription = ""

    private val binder = MusicServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        songUrl = intent?.getStringExtra(SONG_URL_TAG) ?: ""
        songDescription = intent?.getStringExtra(SONG_DESCRIPTION_TAG) ?: ""
        Log.i("playlistMaker", "onBind")
        return binder
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    companion object {
        const val SONG_URL_TAG = "song_url"
        const val SONG_DESCRIPTION_TAG = "song_description"
    }
}