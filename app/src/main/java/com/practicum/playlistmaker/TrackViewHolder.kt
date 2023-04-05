package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
    private val trackNameView: TextView by lazy { itemView.findViewById(R.id.trackName)}
    private val trackInfoView: TextView by lazy { itemView.findViewById(R.id.trackInfo)}
    private val trackImageView: ImageView by lazy { itemView.findViewById(R.id.trackImage) }

    fun bind(model: Track) {
        trackNameView.text = model.trackName
        trackInfoView.text = itemView.context.getString(R.string.track_info,model.artistName, model.trackTime)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_track)
            .centerCrop()
            .transform(RoundedCorners(5))
            .into(trackImageView)
    }
}