package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    val searchHistoryAdapter = SearchHistoryAdapter(getTracks())

    private fun getTracks(): ArrayList<Track> {
        val json = sharedPreferences.getString(App.SEARCH_HISTORY_TRACKS, null) ?: return ArrayList()
        return createTrackListFromJson(json)
    }

    fun addTrack(track: Track) {
        searchHistoryAdapter.addItem(track)
        sharedPreferences.edit()
            .putString(App.SEARCH_HISTORY_TRACKS, createJsonFromTrackList(searchHistoryAdapter.items.toTypedArray()))
            .apply()
    }

    fun clearSearchHistory() {
        searchHistoryAdapter.deleteItems()
        sharedPreferences.edit()
            .remove(App.SEARCH_HISTORY_TRACKS)
            .apply()
    }

    private fun createTrackListFromJson(json: String): ArrayList<Track> {
        val arrayList = ArrayList<Track>()
        for (item in Gson().fromJson(json, Array<Track>::class.java)) {
            arrayList.add(item)
        }
        return arrayList
    }

    private fun createJsonFromTrackList(trackList: Array<Track>): String {
        return Gson().toJson(trackList)
    }

}