package com.practicum.playlistmaker

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlayerActivity : AppCompatActivity() {
    private val track: Track? by lazy { getCurrentTrack()}

    private val playerTrackName: TextView by lazy {findViewById(R.id.playerTrackName)}
    private val playerArtistName: TextView by lazy {findViewById(R.id.playerArtistName)}
    private val playerAlbumInfo: TextView by lazy {findViewById(R.id.playerAlbumInfo)}
    private val playerTrackTimeInfo: TextView by lazy {findViewById(R.id.playerTrackTimeInfo)}
    private val playerTrackYearInfo: TextView by lazy {findViewById(R.id.playerTrackYearInfo)}
    private val playerGenreInfo: TextView by lazy {findViewById(R.id.playerGenreInfo)}
    private val playerCountryInfo: TextView by lazy {findViewById(R.id.playerCountryInfo)}
    private val playerImageView: ImageView by lazy { findViewById(R.id.playerImageView) }
    private val playerAlbumGroup: Group by lazy { findViewById(R.id.playerAlbumGroup) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val btPlayerBack = findViewById<ImageButton>(R.id.btPlayerBack)

        init()
        setVisibility()

        btPlayerBack.setOnClickListener {
            finish()
        }
    }

    private fun getCurrentTrack(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            intent.getParcelableExtra(TRACK)
        }
    }

    private fun init() {
        playerTrackName.text = track?.trackName.orEmpty()
        playerArtistName.text = track?.artistName.orEmpty()
        playerTrackTimeInfo.text = track?.getTrackTime()
        playerAlbumInfo.text = track?.albumName.orEmpty()
        playerCountryInfo.text = track?.country.orEmpty()
        playerTrackYearInfo.text = track?.getReleaseYear().orEmpty()
        playerGenreInfo.text = track?.genreName.orEmpty()
        Glide.with(this)
            .load(track?.getCoverArtwork())
            .placeholder(R.drawable.ic_track)
            .centerCrop()
            .transform(RoundedCorners(32))
            .into(playerImageView)
    }

    private fun setVisibility() {
        playerAlbumGroup.isVisible = !playerAlbumInfo.text.isNullOrEmpty()
    }

    companion object {
        const val TRACK = "Track"
    }
}