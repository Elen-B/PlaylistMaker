package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(var items: MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is Track -> VIEW_TYPE_TRACK
            is PHTrackEmpty -> VIEW_TYPE_EMPTY
            is PHTrackError -> VIEW_TYPE_ERROR
            else -> throw java.lang.IllegalStateException("Cannot find viewType for item $item")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TRACK -> TrackViewHolder(parent)
            VIEW_TYPE_EMPTY -> PHTrackEmptyViewHolder(parent)
            VIEW_TYPE_ERROR -> PHTrackErrorViewHolder(parent)
            else -> throw java.lang.IllegalStateException("Cannot create ViewHolder for viewType $viewType")
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Track -> {
                val trackViewHolder: TrackViewHolder = holder as TrackViewHolder
                trackViewHolder.bind(item)
            }
            is PHTrackEmpty, is PHTrackError -> Unit
            else -> throw java.lang.IllegalStateException("Cannot find viewType for item $item")
        }
    }

    companion object {
        const val VIEW_TYPE_TRACK = 1
        const val VIEW_TYPE_EMPTY = 2
        const val VIEW_TYPE_ERROR = 3
    }
}