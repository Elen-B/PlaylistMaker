package com.practicum.playlistmaker.media.presentation.models

sealed interface PlaylistsScreenState {
    object Empty : PlaylistsScreenState
}