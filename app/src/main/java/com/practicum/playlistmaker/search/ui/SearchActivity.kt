package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
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

    val edSearch: EditText by lazy { findViewById(R.id.edSearch) }
    private val btSearchBack: ImageButton by lazy { findViewById(R.id.btSearchBack) }
    private val btClearSearch: ImageView by lazy { findViewById(R.id.btClearSearch) }
    private val recyclerSearch: RecyclerView by lazy { findViewById(R.id.trackList) }
    private val trackEmptyView: View by lazy { findViewById(R.id.trackEmpty) }
    private val trackErrorView: View by lazy { findViewById(R.id.trackError) }
    private val searchHistoryView: View by lazy { findViewById(R.id.searchHistory) }
    private val recyclerHistory: RecyclerView by lazy { searchHistoryView.findViewById(R.id.SearchList) }
    private val progressBar: View by lazy { findViewById(R.id.progressBar) }
    private var searchText: String? = ""

    private val historyAdapter = TrackAdapter(ArrayList()).apply {
        clickListener = TrackAdapter.TrackClickListener {
            showPlayerActivity(it)
        }
    }

    private val items = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(items).apply {
        clickListener = TrackAdapter.TrackClickListener {
            viewModel.addTrackToSearchHistory(it)
            showPlayerActivity(it)
        }
    }

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel.observeState().observe(this) {
            render(it)
        }

        btSearchBack.setOnClickListener {
            finish()
        }

        btClearSearch.setOnClickListener {
            edSearch.setText("")
            val imm =
                edSearch.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edSearch.windowToken, 0)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btClearSearch.isVisible = !s.isNullOrEmpty()
                viewModel.onEditTextChanged(edSearch.hasFocus(), edSearch.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = edSearch.text.toString()
            }
        }

        edSearch.addTextChangedListener(searchTextWatcher)
        edSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onEditorAction()
            }
            false
        }
        edSearch.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onEditFocusChange(hasFocus)
        }

        trackErrorView.findViewById<Button>(R.id.btRetry).setOnClickListener {
            viewModel.onRetryButtonClick()
        }

        searchHistoryView.findViewById<Button>(R.id.btClearSearchHistory).setOnClickListener {
            viewModel.onClearSearchHistoryButtonClick()
        }

        recyclerSearch.layoutManager = LinearLayoutManager(this)
        recyclerSearch.adapter = trackAdapter

        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.reverseLayout = true
        recyclerHistory.layoutManager = mLayoutManager
        recyclerHistory.adapter = historyAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle
    ) {
        super.onRestoreInstanceState(savedInstanceState)
        edSearch.setText(savedInstanceState.getString(SEARCH_TEXT))
        edSearch.setSelection(edSearch.text.length)
    }

    private fun showPlayerActivity(track: Track) {
        val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
        playerIntent.putExtra("Track", track)
        startActivity(playerIntent)

        //viewModel.showPlayer(track)
    }

    private fun render(state: SearchScreenState) {
        recyclerSearch.isVisible = state is SearchScreenState.List
        trackEmptyView.isVisible = state is SearchScreenState.Empty
        trackErrorView.isVisible = state is SearchScreenState.Error
        searchHistoryView.isVisible = state is SearchScreenState.History
        progressBar.isVisible = state is SearchScreenState.Progress
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