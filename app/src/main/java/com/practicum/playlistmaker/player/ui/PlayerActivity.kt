package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.presentation.mapper.ParcelableTrackMapper
import com.practicum.playlistmaker.player.presentation.models.ParcelableTrack
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track


class PlayerActivity : AppCompatActivity() {
    private lateinit var viewModel: PlayerViewModel

    private val playerTrackName: TextView by lazy { findViewById(R.id.playerTrackName) }
    private val playerArtistName: TextView by lazy { findViewById(R.id.playerArtistName) }
    private val playerAlbumInfo: TextView by lazy { findViewById(R.id.playerAlbumInfo) }
    private val playerTrackTimeInfo: TextView by lazy { findViewById(R.id.playerTrackTimeInfo) }
    private val playerTrackYearInfo: TextView by lazy { findViewById(R.id.playerTrackYearInfo) }
    private val playerGenreInfo: TextView by lazy { findViewById(R.id.playerGenreInfo) }
    private val playerCountryInfo: TextView by lazy { findViewById(R.id.playerCountryInfo) }
    private val playerImageView: ImageView by lazy { findViewById(R.id.playerImageView) }
    private val playerAlbumGroup: Group by lazy { findViewById(R.id.playerAlbumGroup) }
    private val playerTrackTimeProgress: TextView by lazy { findViewById(R.id.playerTrackTimeProgress) }
    private val playerPlayTrack: ImageButton by lazy { findViewById(R.id.playerPlayTrack) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = getCurrentTrack()
        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.factory(track)
        )[PlayerViewModel::class.java]

        viewModel.observeState().observe(this) {
            render(it)
        }

        init(track)
        val btPlayerBack = findViewById<ImageButton>(R.id.btPlayerBack)

        btPlayerBack.setOnClickListener {
            finish()
        }

        playerPlayTrack.setOnClickListener {
            viewModel.playBackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }
    private fun getCurrentTrack(): Track {
        val track: ParcelableTrack? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK, ParcelableTrack::class.java)
        } else {
            intent.getParcelableExtra(TRACK)
        }
        return ParcelableTrackMapper.map(track ?: ParcelableTrack())
    }
    private fun render(state: PlayerScreenState) {
        when (state) {
            is PlayerScreenState.Default -> onGetDefaultState()
            is PlayerScreenState.Prepared -> onGetPreparedState()
            is PlayerScreenState.Playing -> onGetPlayingState()
            is PlayerScreenState.Progress -> onGetProgressState(state.time)
            is PlayerScreenState.Paused -> onGetProgressState()
        }
    }

    private fun init(track : Track) {
        playerTrackName.text = track.trackName.orEmpty()
        playerArtistName.text = track.artistName.orEmpty()
        playerTrackTimeInfo.text = track.trackTime.orEmpty()
        playerAlbumInfo.text = track.albumName.orEmpty()
        playerCountryInfo.text = track.country.orEmpty()
        playerTrackYearInfo.text = track.getReleaseYear()
        playerGenreInfo.text = track.genreName.orEmpty()
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track)
            .centerCrop()
            .transform(
                RoundedCorners(
                    (resources.getDimension(R.dimen.large_album_round_corners) * (resources
                        .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
                )
            )
            .into(playerImageView)

        setVisibility()
    }

    private fun setVisibility() {
        playerAlbumGroup.isVisible = !playerAlbumInfo.text.isNullOrEmpty()
    }
    private fun setTime(time: String?) {
        if (!time.isNullOrEmpty()) {
            playerTrackTimeProgress.text = time
        }
    }
    private fun onGetDefaultState() {
        playerPlayTrack.imageAlpha = GREY_IMAGE_ALPHA_CHANNEL
        playerPlayTrack.isEnabled = false
    }
    private fun onGetPreparedState() {
        playerPlayTrack.setImageResource(R.drawable.ic_play_track)
        playerPlayTrack.imageAlpha = WHITE_IMAGE_ALPHA_CHANNEL
        playerPlayTrack.isEnabled = true
    }
    private fun onGetPlayingState() {
        playerPlayTrack.setImageResource(R.drawable.ic_pause_track)
    }
    private fun onGetProgressState(time: String?) {
        setTime(time)
    }
    private fun onGetProgressState() {
        playerPlayTrack.setImageResource(R.drawable.ic_play_track)
    }
    companion object {
        const val TRACK = "Track"
        private const val GREY_IMAGE_ALPHA_CHANNEL = 75
        private const val WHITE_IMAGE_ALPHA_CHANNEL = 255

        fun show(context: Context, track: Track) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(TRACK, ParcelableTrackMapper.map(track))

            context.startActivity(intent)
        }
    }
}