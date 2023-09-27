package com.practicum.playlistmaker.media.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.media.presentation.models.PlaylistsScreenState
import com.practicum.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {
    private lateinit var binding: FragmentPlaylistsBinding

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.playlistsEmpty.btNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_playlistFragment)
        }
    }

    private fun render(state: PlaylistsScreenState) {
        binding.playlistsEmpty.root.isVisible = state == PlaylistsScreenState.Empty
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}