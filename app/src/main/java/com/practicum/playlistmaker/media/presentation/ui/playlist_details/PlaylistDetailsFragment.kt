package com.practicum.playlistmaker.media.presentation.ui.playlist_details

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.presentation.models.PlaylistDetailsScreenState
import com.practicum.playlistmaker.media.presentation.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.player.presentation.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistDetailsFragment: Fragment() {
    private lateinit var binding: FragmentPlaylistDetailsBinding
    private val args: PlaylistDetailsFragmentArgs by navArgs()

    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(args.playlistId)
    }
    lateinit var deleteTrackDialog: MaterialAlertDialogBuilder

    private val trackListBottomSheetAdapter = TrackListBottomSheetAdapter(ArrayList()).apply {
        clickListener = TrackListBottomSheetAdapter.TrackClickListener {
            viewModel.showPlayer(it)
        }

        longClickListener = TrackListBottomSheetAdapter.TrackLongClickListener {track ->
            deleteTrackDialog
                .setPositiveButton(R.string.delete_track_dialog_yes_button) { _, _ ->
                    viewModel.onDeleteTrackClick(track)
                }
                .show()
        }
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistDetailsTracksBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.playlistDetailsTracksBottomSheet.doOnNextLayout {
            bottomSheetBehavior.peekHeight = getPeekHeight()
        }

        binding.rvPlaylistTrackList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        binding.rvPlaylistTrackList.adapter = trackListBottomSheetAdapter

        viewModel.observeTrackList().observe(viewLifecycleOwner) { trackList ->
            renderTrackList(trackList)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.getShowPlayerTrigger().observe(viewLifecycleOwner) {
            showPlayerActivity(it)
        }

        binding.btPlaylistDetailsBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btPlaylistDetailsShare.setOnClickListener {
            if (!viewModel.onSharePlaylist()) {
                showMessage(getString(R.string.share_playlist_empty_message))
            }
        }

        deleteTrackDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_dialog_title))
            .setMessage(R.string.delete_track_dialog_message)
            .setNegativeButton(R.string.delete_track_dialog_cancel_button) {_, _ -> }
            .setPositiveButton(R.string.delete_track_dialog_yes_button) { _, _ -> }
    }

    private fun renderState(state: PlaylistDetailsScreenState) {
        when (state) {
            is PlaylistDetailsScreenState.Content -> setContent(state.data)
            is PlaylistDetailsScreenState.Error -> Unit
            else -> Unit
        }
    }

    private fun renderTrackList(trackList: List<Track>) {
        binding.tvPlaylistDetailsStatistics.text = getString(
            R.string.playlist_statistics,
            viewModel.getPlaylistTimeStatistics(),
            viewModel.getTrackCountStatistics()
        )
        trackListBottomSheetAdapter.addItems(trackList)

        binding.tvPlaylistTrackEmpty.isVisible = trackList.isEmpty()
        binding.rvPlaylistTrackList.isVisible = trackList.isNotEmpty()
    }

    private fun setContent(playlist: Playlist) {
        binding.tvPlaylistDetailsName.text = playlist.name.orEmpty()
        binding.tvPlaylistDetailsDescription.text = playlist.description.orEmpty()
        binding.playlistDetailsImageView.setImageResource(R.drawable.ic_playlist_large)
        if (!playlist.filePath.isNullOrEmpty()) {
            val file = playlist.filePath?.let { File(viewModel.getImageDirectory(), it) }
            binding.playlistDetailsImageView.setImageURI(Uri.fromFile(file))
            binding.playlistDetailsImageView.clipToOutline = true
        }

        bottomSheetBehavior.peekHeight = getPeekHeight()
    }

    private fun getPeekHeight(): Int {
        val openMenuLocation = IntArray(2)
        binding.llPlaylistDetailsToolbar.getLocationOnScreen(openMenuLocation)
        return binding.root.height - openMenuLocation[1] - resources.getDimensionPixelSize(R.dimen.small_medium_margin)
    }

    private fun showPlayerActivity(track: Track) {
        findNavController().navigate(R.id.action_playlistDetailsFragment_to_playerActivity,
            PlayerActivity.createArgs(track))
    }

    private fun showMessage(message: String) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}