package com.practicum.playlistmaker.search.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.presentation.models.SearchScreenState
import com.practicum.playlistmaker.search.presentation.utils.SingleEventLiveData

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchScreenState>()
    fun observeState(): LiveData<SearchScreenState> = stateLiveData

    private val showPlayerTrigger = SingleEventLiveData<Track>()
    fun getShowPlayerTrigger(): LiveData<Track> = showPlayerTrigger

    private var lastSearchText: String? = null
    private var currentSearchText: String? = null

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        search(newSearchText)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

    private fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun setState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    private fun search(searchText: String) {
        if (searchText.isEmpty()) {
            setState(SearchScreenState.List(ArrayList()))
            return
        }
        setState(SearchScreenState.Progress)

        trackInteractor.search(searchText, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?) {
                val tracks = ArrayList<Track>()
                if (foundTracks != null) {
                    tracks.addAll(foundTracks)
                }

                when {
                    errorMessage != null -> {
                        setState(SearchScreenState.Error)
                    }

                    tracks.isEmpty() -> {
                        setState(SearchScreenState.Empty)
                    }

                    else -> {
                        setState(SearchScreenState.List(tracks = tracks))
                    }
                }
            }
        })
    }


    fun onRetryButtonClick() {
        search(currentSearchText ?: "")
    }

    fun onEditTextChanged(hasFocus: Boolean, text: String?) {
        currentSearchText = text
        val tracks = getHistoryTrackList()
        if (hasFocus && currentSearchText?.isEmpty() == true && tracks.size > 0) {
            handler.removeCallbacks(searchRunnable)
            setState(SearchScreenState.History(tracks))
        } else {
            searchDebounce(currentSearchText ?: "")
            setState(
                SearchScreenState.List(
                    if (stateLiveData.value is SearchScreenState.List) {
                        (stateLiveData.value as SearchScreenState.List).tracks
                    } else {
                        ArrayList()
                    }
                )
            )
        }
    }

    fun onEditFocusChange(hasFocus: Boolean) {
        val tracks = getHistoryTrackList()
        if (hasFocus && currentSearchText.isNullOrEmpty() && tracks.size > 0) {
            setState(SearchScreenState.History(tracks))
        } else {
            setState(
                SearchScreenState.List(
                    if (stateLiveData.value is SearchScreenState.List) {
                        (stateLiveData.value as SearchScreenState.List).tracks
                    } else {
                        ArrayList()
                    }
                )
            )
        }
    }

    fun onClearSearchHistoryButtonClick() {
        clearHistoryTrackList()
        setState(SearchScreenState.List(ArrayList()))
    }

    fun onEditorAction() {
        search(currentSearchText ?: "")
    }

    fun showPlayer(track: Track) {
        if (clickDebounce()) {
            showPlayerTrigger.value = track
        }
    }

    fun addTrackToSearchHistory(track: Track) {
        historyInteractor.addTrackToSearchHistory(track)
    }

    private fun getHistoryTrackList(): ArrayList<Track> {
        return historyInteractor.getSearchHistory()
    }

    private fun clearHistoryTrackList() {
        historyInteractor.clearSearchHistory()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}