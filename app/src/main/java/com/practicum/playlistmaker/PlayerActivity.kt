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
            intent.getParcelableExtra("Track", Track::class.java)
        } else {
            intent.getParcelableExtra("Track")
        }
    }

    private fun init() {
        playerTrackName.text = track?.trackName ?: ""
        playerArtistName.text = track?.artistName ?: ""
        playerTrackTimeInfo.text = track?.getTrackTime()
        playerAlbumInfo.text = track?.albumName ?: ""
        playerCountryInfo.text = track?.country ?: ""
        playerTrackYearInfo.text = track?.geReleaseYear() ?: ""
        playerGenreInfo.text = track?.genreName ?: ""
        Glide.with(this)
            .load(track?.getCoverArtwork())
            .placeholder(R.drawable.ic_track)
            .centerCrop()
            .transform(RoundedCorners(32))
            .into(playerImageView)
    }

    private fun setVisibility() {
        findViewById<Group>(R.id.playerAlbumGroup).isVisible = !playerAlbumInfo.text.isNullOrEmpty()
    }
}