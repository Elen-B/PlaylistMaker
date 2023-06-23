package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator.provideTrackInteractor
import com.practicum.playlistmaker.domain.api.TrackInteractor
import com.practicum.playlistmaker.domain.models.SearchResultView
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.PlayerActivity


class SearchActivity : AppCompatActivity() {
    private val trackProvider = provideTrackInteractor()

    val edSearch: EditText by lazy { findViewById(R.id.edSearch) }
    private val btSearchBack: ImageButton by lazy { findViewById(R.id.btSearchBack) }
    private val btClearSearch: ImageView by lazy { findViewById(R.id.btClearSearch) }
    private val recyclerSearch: RecyclerView by lazy { findViewById(R.id.trackList) }
    private val trackEmptyView: View by lazy { findViewById(R.id.trackEmpty) }
    private val trackErrorView: View by lazy { findViewById(R.id.trackError) }
    private val searchHistoryView: View by lazy { findViewById(R.id.searchHistory) }
    private val recyclerHistory: RecyclerView by lazy { searchHistoryView.findViewById(R.id.SearchList) }
    private val searchHistory: SearchHistory by lazy { SearchHistory((applicationContext as App).appPreferences) }
    private val progressBar: View by lazy { findViewById(R.id.progressBar) }
    private var searchText: String? = ""

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable =
        Runnable { if (!edSearch.text.isNullOrEmpty()) searchTrack(edSearch.text.toString()) }
    private var isClickAllowed = true

    private val historyAdapter = TrackAdapter(ArrayList()).apply {
        clickListener = TrackAdapter.TrackClickListener {
            if (clickDebounce()) {
                showPlayerActivity(it)
            }
        }
    }

    private val items = ArrayList<Track>()
    val trackAdapter = TrackAdapter(items).apply {
        clickListener = TrackAdapter.TrackClickListener {
            searchHistory.addTrack(it)
            historyAdapter.addItems(searchHistory.trackHistoryList)
            if (clickDebounce()) {
                showPlayerActivity(it)
            }
        }
    }

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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
                if (edSearch.hasFocus() && s?.isEmpty() == true && searchHistory.trackHistoryList.size > 0) {
                    showSearchResultView(SearchResultView.HISTORY)
                } else {
                    searchDebounce()
                    showSearchResultView(SearchResultView.LIST)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = edSearch.text.toString()
            }
        }

        edSearch.addTextChangedListener(searchTextWatcher)
        edSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack(edSearch.text.toString())
            }
            false
        }
        edSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && edSearch.text.isEmpty() && searchHistory.trackHistoryList.size > 0) {
                showSearchResultView(SearchResultView.HISTORY)
            } else {
                showSearchResultView(SearchResultView.LIST)
            }
        }

        trackErrorView.findViewById<Button>(R.id.btRetry).setOnClickListener {
            searchTrack(edSearch.text.toString())
        }

        searchHistoryView.findViewById<Button>(R.id.btClearSearchHistory).setOnClickListener {
            searchHistory.clearSearchHistory()
            historyAdapter.deleteItems()
            showSearchResultView(SearchResultView.LIST)
        }

        recyclerSearch.layoutManager = LinearLayoutManager(this)
        recyclerSearch.adapter = trackAdapter

        historyAdapter.addItems(searchHistory.trackHistoryList)
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

    private fun searchTrack(searchText: String) {
        showSearchResultView(SearchResultView.PROGRESS)
        trackProvider.search(searchText, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: ArrayList<Track>) {
                if (foundTracks.isNotEmpty()) {
                    handler.post {
                        showSearchResultView(SearchResultView.LIST)
                        trackAdapter.addItems(foundTracks)
                    }
                }
            }

            override fun onEmpty() {
                handler.post {
                    Log.e("onFailure", "EMPTY")
                    showSearchResultView(SearchResultView.EMPTY)
                }
            }

            override fun onFailure() {
                handler.post {
                    Log.e("onFailure", "onFailure")
                    showSearchResultView(SearchResultView.ERROR)
                }
            }
        })
    }

    private fun showSearchResultView(viewType: SearchResultView) {
        if (viewType == SearchResultView.LIST) {
            trackAdapter.deleteItems()
        }
        recyclerSearch.isVisible = viewType == SearchResultView.LIST
        trackEmptyView.isVisible = viewType == SearchResultView.EMPTY
        trackErrorView.isVisible = viewType == SearchResultView.ERROR
        searchHistoryView.isVisible = viewType == SearchResultView.HISTORY
        progressBar.isVisible = viewType == SearchResultView.PROGRESS
    }

    private fun showPlayerActivity(track: Track) {
        val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
        playerIntent.putExtra("Track", track)
        startActivity(playerIntent)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}