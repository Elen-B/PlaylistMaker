package com.practicum.playlistmaker.search.domain.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Track(
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val trackTime: String?,
    val artworkUrl100: String?,
    val albumName: String?,
    val releaseDate: Date?,
    val genreName: String?,
    val country: String?,
    val previewUrl: String?
) {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    fun getReleaseYear(): String =
        SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate!!).orEmpty()

}