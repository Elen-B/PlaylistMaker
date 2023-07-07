package com.practicum.playlistmaker.player.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerViewModel(private val track: Track): ViewModel() {

    companion object {
        fun factory(track: Track): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    PlayerViewModel(track)
                }
            }
        }
    }
}