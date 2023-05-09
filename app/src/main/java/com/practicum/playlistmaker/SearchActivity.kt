package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {
    private val iTunesAPIService = ITunesApi.create()

    val edSearch: EditText by lazy { findViewById(R.id.edSearch) }
    private val btSearchBack: ImageButton by lazy {findViewById(R.id.btSearchBack)}
    private val btClearSearch: ImageView by lazy {findViewById(R.id.btClearSearch)}
    private val recyclerSearch: RecyclerView by lazy { findViewById(R.id.trackList)}
    private val trackEmptyView: View by lazy { findViewById(R.id.trackEmpty)}
    private val trackErrorView: View by lazy { findViewById(R.id.trackError)}
    private val searchHistoryView: View by lazy { findViewById(R.id.searchHistory)}
    private val recyclerHistory: RecyclerView by lazy { searchHistoryView.findViewById(R.id.SearchList)}
    private val searchHistory: SearchHistory by lazy { SearchHistory((applicationContext as App).appPreferences)}
    private var searchText: String? = ""

    private val historyAdapter = TrackAdapter(ArrayList()).apply {
        clickListener = TrackAdapter.TrackClickListener {
        }
    }

    private val items = ArrayList<Track>()
    val trackAdapter = TrackAdapter(items).apply {
        clickListener = TrackAdapter.TrackClickListener {
            searchHistory.addTrack(it)
            historyAdapter.addItems(searchHistory.trackHistoryList)
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
            val imm = edSearch.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edSearch.windowToken, 0)
        }

        val searchTextWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btClearSearch.isVisible = !s.isNullOrEmpty()
                if (edSearch.hasFocus() && s?.isEmpty() == true && searchHistory.trackHistoryList.size > 0) {
                    showSearchResultView(SearchResultView.HISTORY)
                }
                else {
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
            }
            else {
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
        savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        edSearch.setText(savedInstanceState.getString(SEARCH_TEXT))
        edSearch.setSelection(edSearch.text.length)
    }

    private fun searchTrack(searchText: String) {
        iTunesAPIService.search(searchText)
            .enqueue(object: Callback<TrackSearchResponse> {
                override fun onResponse(
                    call: Call<TrackSearchResponse>,
                    response: Response<TrackSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            showSearchResultView(SearchResultView.LIST)
                            trackAdapter.addItems(response.body()?.results!!)
                        }
                        else {
                            showSearchResultView(SearchResultView.EMPTY)
                        }
                    }
                    else {
                        showSearchResultView(SearchResultView.ERROR)
                    }
                }

                override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                    showSearchResultView(SearchResultView.ERROR)
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
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}