package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TrackRepository {
    fun search(expression: String, onEmpty: () -> Unit, onFailure: ()->Unit): ArrayList<Track>
}