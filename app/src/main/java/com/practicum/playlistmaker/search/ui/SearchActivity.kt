package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.presentation.models.SearchScreenState
import com.practicum.playlistmaker.search.presentation.view_model.SearchViewModel


class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory((applicationContext as App).appPreferences)
        )[SearchViewModel::class.java]
    }
    private lateinit var binding: ActivitySearchBinding

    private var searchText: String? = ""

    private val historyAdapter = TrackAdapter(ArrayList()).apply {
        clickListener = TrackAdapter.TrackClickListener {
            viewModel.showPlayer(it)
        }
    }

    private val items = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(items).apply {
        clickListener = TrackAdapter.TrackClickListener {
            viewModel.addTrackToSearchHistory(it)
            viewModel.showPlayer(it)
        }
    }

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.getShowPlayerTrigger().observe(this) {
            showPlayerActivity(it)
        }

        binding.btSearchBack.setOnClickListener {
            finish()
        }

        binding.btClearSearch.setOnClickListener {
            binding.edSearch.setText("")
            val imm =
                binding.edSearch.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edSearch.windowToken, 0)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btClearSearch.isVisible = !s.isNullOrEmpty()
                viewModel.onEditTextChanged(binding.edSearch.hasFocus(), binding.edSearch.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = binding.edSearch.text.toString()
            }
        }

        binding.edSearch.addTextChangedListener(searchTextWatcher)
        binding.edSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onEditorAction()
            }
            false
        }
        binding.edSearch.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onEditFocusChange(hasFocus)
        }

        binding.trackError.btRetry.setOnClickListener {
            viewModel.onRetryButtonClick()
        }

        binding.searchHistory.btClearSearchHistory.setOnClickListener {
            viewModel.onClearSearchHistoryButtonClick()
        }

        binding.trackList.layoutManager = LinearLayoutManager(this)
        binding.trackList.adapter = trackAdapter

        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.reverseLayout = true
        binding.searchHistory.SearchList.layoutManager = mLayoutManager
        binding.searchHistory.SearchList.adapter = historyAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle
    ) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.edSearch.setText(savedInstanceState.getString(SEARCH_TEXT))
        binding.edSearch.setSelection(binding.edSearch.text.length)
    }

    private fun showPlayerActivity(track: Track) {
        PlayerActivity.show(this, track)
    }

    private fun render(state: SearchScreenState) {
        binding.trackList.isVisible = state is SearchScreenState.List
        binding.trackEmpty.root.isVisible = state is SearchScreenState.Empty
        binding.trackError.root.isVisible = state is SearchScreenState.Error
        binding.searchHistory.root.isVisible = state is SearchScreenState.History
        binding.progressBar.isVisible = state is SearchScreenState.Progress
        when (state) {
            is SearchScreenState.List -> trackAdapter.addItems(state.tracks)
            is SearchScreenState.Empty -> Unit
            is SearchScreenState.Error -> Unit
            is SearchScreenState.History -> historyAdapter.addItems(state.tracks)
            is SearchScreenState.Progress -> Unit
        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}