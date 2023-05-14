package com.practicum.playlistmaker

import android.os.Parcel
import android.os.Parcelable
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

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    val artworkUrl100: String,
    @SerializedName("collectionName") val albumName: String?,
    val releaseDate: Date,
    @SerializedName("primaryGenreName") val genreName: String,
    val country: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString(),
        Date(parcel.readLong()),
        parcel.readString()!!,
        parcel.readString()
    )

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    fun getTrackTime(): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
    fun geReleaseYear(): String = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(trackId)
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeLong(trackTime)
        parcel.writeString(artworkUrl100)
        parcel.writeString(albumName)
        parcel.writeLong(releaseDate.time)
        parcel.writeString(genreName)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}

class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.track_view, parentView, false)
) {

    private val trackNameView: TextView by lazy { itemView.findViewById(R.id.trackName) }
    private val trackInfoView: TextView by lazy { itemView.findViewById(R.id.trackInfo) }
    private val trackImageView: ImageView by lazy { itemView.findViewById(R.id.trackImage) }

    fun bind(model: Track) {
        trackNameView.text = model.trackName
        trackInfoView.text = itemView.context.getString(
            R.string.track_info,
            model.artistName,
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime)
        )
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_track)
            .centerCrop()
            .transform(RoundedCorners(5))
            .into(trackImageView)
    }
}
