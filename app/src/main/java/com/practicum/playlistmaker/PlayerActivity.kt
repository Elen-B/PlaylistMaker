package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale


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
    private val playerTrackTimeProgress: TextView by lazy {findViewById(R.id.playerTrackTimeProgress)}
    private val playerPlayTrack: ImageButton by lazy {findViewById(R.id.playerPlayTrack)}

    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val btPlayerBack = findViewById<ImageButton>(R.id.btPlayerBack)

        init()
        setVisibility()
        preparePlayer()

        btPlayerBack.setOnClickListener {
            finish()
        }

        playerPlayTrack.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
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
            .transform(RoundedCorners(
                (resources.getDimension(R.dimen.large_album_round_corners) * (resources
                    .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
            ))
            .into(playerImageView)
    }

    private fun setVisibility() {
        playerAlbumGroup.isVisible = !playerAlbumInfo.text.isNullOrEmpty()
    }

    private fun preparePlayer() {
        try {
            mediaPlayer.setDataSource(track?.previewUrl.orEmpty())
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            playerPlayTrack.imageAlpha = GREY_IMAGE_ALPHA_CHANNEL
            playerPlayTrack.isEnabled = false
            return
        }
        mediaPlayer.setOnPreparedListener {
            playerPlayTrack.setImageResource(R.drawable.ic_play_track)
            playerPlayTrack.imageAlpha = WHITE_IMAGE_ALPHA_CHANNEL
            playerPlayTrack.isEnabled = true
            playerState = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            stopTimerTask()
            playerPlayTrack.setImageResource(R.drawable.ic_play_track)
            playerState = PlayerState.PREPARED
        }
    }

    private fun startPlayer() {
        if (playerState != PlayerState.DEFAULT) {
            mediaPlayer.start()
            playerPlayTrack.setImageResource(R.drawable.ic_pause_track)
            playerState = PlayerState.PLAYING
            handler.post(runTimerTask())
        }
    }

    private fun pausePlayer() {
        if (playerState != PlayerState.DEFAULT) {
            mediaPlayer.pause()
            playerPlayTrack.setImageResource(R.drawable.ic_play_track)
            playerState = PlayerState.PAUSED
            stopTimerTask()
        }
    }

    private fun playbackControl() {
        when(playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            else -> {}
        }
    }
    private fun setTime() {
        when (playerState) {
            PlayerState.PLAYING -> {
                playerTrackTimeProgress.text =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            }
            PlayerState.PREPARED, PlayerState.DEFAULT -> {
                playerTrackTimeProgress.text = getString(R.string.player_time_progress_default)
            }
            else -> {}
        }
    }

    private fun runTimerTask(): Runnable {
        return Runnable {
            setTime()
            handler.postDelayed(runTimerTask(), TIME_DEBOUNCE_DELAY)
        }
    }

    private fun stopTimerTask(){
        handler.removeCallbacks(runTimerTask())
    }

    companion object {
        const val TRACK = "Track"
        private const val TIME_DEBOUNCE_DELAY = 500L
        private const val GREY_IMAGE_ALPHA_CHANNEL = 75
        private const val WHITE_IMAGE_ALPHA_CHANNEL = 255
    }
}