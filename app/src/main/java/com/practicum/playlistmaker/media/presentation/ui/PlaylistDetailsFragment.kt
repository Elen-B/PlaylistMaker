package com.practicum.playlistmaker.media.presentation.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.presentation.models.PlaylistDetailsScreenState
import com.practicum.playlistmaker.media.presentation.view_model.PlaylistDetailsViewModel
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

        viewModel.observeTrackList().observe(viewLifecycleOwner) { trackList ->
            renderTrackList(trackList)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        binding.btPlaylistDetailsBack.setOnClickListener {
            findNavController().navigateUp()
        }
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
    }

    private fun setContent(playlist: Playlist) {
        binding.tvPlaylistDetailsName.text = playlist.name.orEmpty()
        binding.tvPlaylistDetailsDescription.isVisible = !playlist.description.isNullOrEmpty()
        binding.tvPlaylistDetailsDescription.text = playlist.description.orEmpty()
        binding.playlistDetailsImageView.setImageResource(R.drawable.ic_playlist_large)
        if (!playlist.filePath.isNullOrEmpty()) {
            val file = playlist.filePath?.let { File(viewModel.getImageDirectory(), it) }
            binding.playlistDetailsImageView.setImageURI(Uri.fromFile(file))
            binding.playlistDetailsImageView.clipToOutline = true
        }
    }
}