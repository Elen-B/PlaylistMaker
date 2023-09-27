package com.practicum.playlistmaker.media.presentation.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.presentation.models.PlaylistScreenState
import com.practicum.playlistmaker.media.presentation.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class PlaylistFragment: Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val viewModel: PlaylistViewModel by viewModel()

    private val backPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.needShowDialog()) {
                confirmDialog.show()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickImage =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                viewModel.onPlaylistImageChanged(uri)
                setPlaylistImage(uri)
                requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
            }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.getAddPlaylistTrigger().observe(viewLifecycleOwner) {
            addPlaylist(it)
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.create_playlist_dialog_title))
            .setMessage(R.string.create_playlist_dialog_message)
            .setNegativeButton(R.string.create_playlist_dialog_cancel_button) {_, _ -> }
            .setPositiveButton(R.string.create_playlist_dialog_yes_button) { _, _ ->
                findNavController().navigateUp()
            }

        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)

        binding.btPlaylistBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.edPlaylistName.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onPlaylistNameChanged(text.toString())
        }

        binding.edPlaylistDescription.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onPlaylistDescriptionChanged(text.toString())
        }

        binding.playlistImageView.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btCreatePlaylist.setOnClickListener {
            viewModel.onAddPlaylistClick()
        }
    }

    private fun render(state: PlaylistScreenState) {
        binding.btCreatePlaylist.isEnabled = state is PlaylistScreenState.Filled
        setEditTextStyle(binding.edPlaylistName, state is PlaylistScreenState.Filled)

        val content: Playlist? = when (state) {
            is PlaylistScreenState.Filled -> state.content
            is PlaylistScreenState.NotEmpty -> state.content
            else -> null
        }
        setEditTextStyle(binding.edPlaylistDescription, !content?.description.isNullOrEmpty())
    }

    private fun setEditTextStyle(textInputLayout: TextInputLayout, filled: Boolean) {
        val coloInt =  if (filled) R.color.edittext_playlist_selector_text else R.color.edittext_playlist_selector
        val colorStateList = ResourcesCompat.getColorStateList(resources, coloInt, requireContext().theme)
        textInputLayout.setBoxStrokeColorStateList(colorStateList!!)
        textInputLayout.defaultHintTextColor = colorStateList
        textInputLayout.hintTextColor = colorStateList
    }

    private fun setPlaylistImage(uri: Uri?) {
        if (uri != null) {
            binding.playlistImageView.setImageURI(uri)
            binding.playlistImageView.clipToOutline = true
            binding.playlistImageView.tag = uri
        }
    }

    private fun addPlaylist(playlist: Playlist) {
        if (binding.playlistImageView.tag != null && !playlist.filePath.isNullOrEmpty()) {
            val uri : Uri = binding.playlistImageView.tag as Uri
            saveImageToPrivateStorage(uri, playlist.filePath!!)
        }

        viewModel.addPlaylist(playlist)

        Toast.makeText(requireContext(), "Плейлист " + playlist.name + " создан", Toast.LENGTH_LONG).show()
        findNavController().navigateUp()
    }

    private fun saveImageToPrivateStorage(uri: Uri, fileName: String) {
        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), IMAGE_DIR)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, fileName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    companion object {
        const val IMAGE_DIR = "playlistImage"
    }
}