package com.practicum.playlistmaker

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class SearchHistory(private val sharedPreferences: SharedPreferences) {
    var trackHistoryList = getTracks()

    private fun getTracks(): ArrayList<Track> {
        val json = sharedPreferences.getString(App.SEARCH_HISTORY_TRACKS, null) ?: return ArrayList()
        return createTrackListFromJson(json)
    }

    fun addTrack(track: Track) {
        val trackInd = trackHistoryList.indexOf(track)
        if (trackInd > -1) {
            trackHistoryList.removeAt(trackInd)
        }
        if (trackHistoryList.size > 9) {
            trackHistoryList.removeAt(0)
        }
        trackHistoryList.add(track)
        sharedPreferences.edit()
            .putString(App.SEARCH_HISTORY_TRACKS, createJsonFromTrackList(trackHistoryList.toTypedArray()))
            .apply()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit {
            remove(App.SEARCH_HISTORY_TRACKS)
        }
    }

    private fun createTrackListFromJson(json: String): ArrayList<Track> {
        val typeOfTrackList: Type = object : TypeToken<ArrayList<Track?>?>() {}.type
        return Gson().fromJson(json, typeOfTrackList)
    }

    private fun createJsonFromTrackList(trackList: Array<Track>): String {
        return Gson().toJson(trackList)
    }

}