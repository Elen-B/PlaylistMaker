package com.practicum.playlistmaker.player.presentation.ui

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.media.presentation.ui.PlaylistFragment
import com.practicum.playlistmaker.player.presentation.mapper.ParcelableTrackMapper
import com.practicum.playlistmaker.player.presentation.models.ParcelableTrack
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenMode
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.player.presentation.models.TrackAddProcessStatus
import com.practicum.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf


class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    private val bottomSheetAdapter = BottomSheetAdapter(ArrayList()).apply {
        clickListener = BottomSheetAdapter.PlaylistClickListener {
            viewModel.addTrackToPlaylist(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.playlistListView.rvPlaylistList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.playlistListView.rvPlaylistList.adapter = bottomSheetAdapter

        supportFragmentManager.setFragmentResultListener(PlaylistFragment.RESULT_KEY, this) {
            _, bundle ->
                val result = bundle.getLong(PlaylistFragment.KEY_PLAYLIST_ID)
                val resultName = bundle.getString(PlaylistFragment.KEY_PLAYLIST_NAME)
            if (result>0) {
                viewModel.addTrackToPlaylist(result, resultName)
            } else {
                viewModel.onPlayerAddTrackClick()
            }
        }

        val track = getCurrentTrack()
        viewModel = getKoin().get { parametersOf(track) }

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeFavourite().observe(this) {
            renderFavourite(it)
        }

        viewModel.observeMode().observe(this) {
            renderMode(it)
        }

        viewModel.observeAddProcessStatus().observe(this) {
            renderAddProcessStatus(it)
        }

        init(track)

        binding.btPlayerBack.setOnClickListener {
            finish()
        }

        binding.playerPlayTrack.setOnClickListener {
            viewModel.playBackControl()
        }

        binding.playerLikeTrack.setOnClickListener {
            viewModel.onLikeTrackClick()
        }

        val bottomSheetContainer = binding.playlistsBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.playerAddTrack.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.onPlayerAddTrackClick()
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.playlistListView.btNewPlaylist.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.player_container_view, PlaylistFragment(), FRAGMENT_TAG)
                .commit()

            viewModel.onNewPlaylistClick()
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

    private fun renderMode(mode: PlayerScreenMode) {
        binding.playerContainerView.isVisible = mode is PlayerScreenMode.NewPlaylist
        binding.svPlayer.isVisible = mode is PlayerScreenMode.Player || mode is PlayerScreenMode.BottomSheet
        binding.overlay.isVisible = mode is PlayerScreenMode.BottomSheet
        binding.playlistsBottomSheet.isVisible = mode is PlayerScreenMode.BottomSheet

        if (mode is PlayerScreenMode.BottomSheet) {
            bottomSheetAdapter.addItems(mode.playlists)
        }
    }

    private fun renderAddProcessStatus(status: TrackAddProcessStatus) {
        when (status) {
            is TrackAddProcessStatus.Added -> showMessage(getString(R.string.add_status_added, status.name))
            is TrackAddProcessStatus.Error -> showMessage(getString(R.string.add_status_error, status.name))
            is TrackAddProcessStatus.Exist -> showMessage(getString(R.string.add_status_exist, status.name))

            else -> Unit
        }
    }

    private fun init(track : Track) {
        binding.playerTrackName.text = track.trackName.orEmpty()
        binding.playerArtistName.text = track.artistName.orEmpty()
        binding.playerTrackTimeInfo.text = track.trackTime.orEmpty()
        binding.playerAlbumInfo.text = track.albumName.orEmpty()
        binding.playerCountryInfo.text = track.country.orEmpty()
        binding.playerTrackYearInfo.text = track.releaseYear.toString()
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

    private fun renderFavourite(isFavourite: Boolean) {
        if (isFavourite) {
            binding.playerLikeTrack.setImageResource(R.drawable.ic_like_track_checked)
        } else {
            binding.playerLikeTrack.setImageResource(R.drawable.ic_like_track)
        }
    }

    private fun showMessage(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    companion object {
        const val TRACK = "Track"
        const val FRAGMENT_TAG = "player"
        private const val GREY_IMAGE_ALPHA_CHANNEL = 75
        private const val WHITE_IMAGE_ALPHA_CHANNEL = 255

        fun createArgs(track: Track): Bundle =
            bundleOf(TRACK to ParcelableTrackMapper.map(track))
    }
}