package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.presentation.mapper.ParcelableTrackMapper
import com.practicum.playlistmaker.player.presentation.models.ParcelableTrack
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track


class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val track = getCurrentTrack()
        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.factory(track)
        )[PlayerViewModel::class.java]

        viewModel.observeState().observe(this) {
            render(it)
        }

        init(track)

        binding.btPlayerBack.setOnClickListener {
            finish()
        }

        binding.playerPlayTrack.setOnClickListener {
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
            is PlayerScreenState.Paused -> onGetPausedState(state.time)
        }
    }

    private fun init(track : Track) {
        binding.playerTrackName.text = track.trackName.orEmpty()
        binding.playerArtistName.text = track.artistName.orEmpty()
        binding.playerTrackTimeInfo.text = track.trackTime.orEmpty()
        binding.playerAlbumInfo.text = track.albumName.orEmpty()
        binding.playerCountryInfo.text = track.country.orEmpty()
        binding.playerTrackYearInfo.text = track.getReleaseYear()
        binding.playerGenreInfo.text = track.genreName.orEmpty()
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
            .into(binding.playerImageView)

        setVisibility()
    }

    private fun setVisibility() {
        binding.playerAlbumGroup.isVisible = !binding.playerAlbumInfo.text.isNullOrEmpty()
    }
    private fun setTime(time: String?) {
        if (!time.isNullOrEmpty()) {
            binding.playerTrackTimeProgress.text = time
        }
    }
    private fun onGetDefaultState() {
        binding.playerPlayTrack.imageAlpha = GREY_IMAGE_ALPHA_CHANNEL
        binding.playerPlayTrack.isEnabled = false
    }
    private fun onGetPreparedState() {
        binding.playerPlayTrack.setImageResource(R.drawable.ic_play_track)
        binding.playerPlayTrack.imageAlpha = WHITE_IMAGE_ALPHA_CHANNEL
        binding.playerPlayTrack.isEnabled = true
    }
    private fun onGetPlayingState() {
        binding.playerPlayTrack.setImageResource(R.drawable.ic_pause_track)
    }
    private fun onGetProgressState(time: String?) {
        setTime(time)
    }
    private fun onGetPausedState(time: String?) {
        setTime(time)
        binding.playerPlayTrack.setImageResource(R.drawable.ic_play_track)
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