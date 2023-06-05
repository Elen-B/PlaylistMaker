package com.practicum.playlistmaker

import android.os.Parcel
import android.os.Parcelable
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
    val country: String?,
    val previewUrl: String?
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
        parcel.readString(),
        parcel.readString()
    )

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    fun getTrackTime(): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
    fun getReleaseYear(): String = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
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
        parcel.writeString(previewUrl)
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