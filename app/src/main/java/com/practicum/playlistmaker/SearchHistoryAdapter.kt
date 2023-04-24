package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryAdapter(var items: ArrayList<Track>) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItem(track: Track) {
        val trackInd = this.items.indexOf(track)
        if (trackInd > -1) {
            this.items.removeAt(trackInd)
            this.notifyItemRemoved(trackInd)
        }
        if (this.itemCount > 9) {
            this.items.removeAt(0)
            this.notifyItemRemoved(0)
        }
        this.items.add(track)
        this.notifyItemInserted(this.itemCount)
    }

    fun deleteItems() {
        this.items.clear()
        this.notifyDataSetChanged()
    }
}