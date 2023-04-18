package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private var items: ArrayList<Track>) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(values: ArrayList<Track>) {
        this.items.clear()
        if (values.size > 0) {
            this.items.addAll(values)
        }
        this.notifyDataSetChanged()
    }

    fun deleteItems() {
        this.items.clear()
        this.notifyDataSetChanged()
    }
}