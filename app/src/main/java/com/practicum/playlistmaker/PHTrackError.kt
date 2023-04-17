package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class PHTrackError

class PHTrackErrorViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.ph_track_error_view, parentView, false)
) {
    private val btRetry = itemView.findViewById<Button>(R.id.btRetry)

    init {
        btRetry.setOnClickListener {
            val text = (it.context as (SearchActivity)).edSearch.text.toString()
            (it.context as (SearchActivity)).searchTrack(text)
        }
    }
}