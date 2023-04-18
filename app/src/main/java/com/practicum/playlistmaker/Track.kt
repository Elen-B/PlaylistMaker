package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Track(val trackName: String, val artistName: String, @SerializedName("trackTimeMillis") val trackTime: Long, val artworkUrl100: String)

class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
    .inflate(R.layout.track_view, parentView, false))  {

    private val trackNameView: TextView by lazy { itemView.findViewById(R.id.trackName)}
    private val trackInfoView: TextView by lazy { itemView.findViewById(R.id.trackInfo)}
    private val trackImageView: ImageView by lazy { itemView.findViewById(R.id.trackImage) }

    fun bind(model: Track) {
        trackNameView.text = model.trackName
        trackInfoView.text = itemView.context.getString(R.string.track_info,model.artistName, SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime))
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_track)
            .centerCrop()
            .transform(RoundedCorners(5))
            .into(trackImageView)
    }
}
