package com.practicum.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.media.presentation.models.FavouritesScreenState

class FavouritesViewModel() : ViewModel() {
    private val stateLiveData = MutableLiveData<FavouritesScreenState>()
    fun observeState(): LiveData<FavouritesScreenState> = stateLiveData

    init {
        setState(FavouritesScreenState.Empty)
    }

    private fun setState(state: FavouritesScreenState) {
        stateLiveData.postValue(state)
    }
}