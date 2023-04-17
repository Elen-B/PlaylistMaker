package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
    private val iTunesAPIService = ITunesAPI.create()

    val edSearch: EditText by lazy { findViewById(R.id.edSearch) }
    private val btSearchBack: ImageButton by lazy {findViewById(R.id.btSearchBack)}
    private val btClearSearch: ImageView by lazy {findViewById(R.id.btClearSearch)}
    private val recyclerSearch: RecyclerView by lazy { findViewById(R.id.trackList)}
    private var searchText: String? = ""

    private val items: MutableList<Any> = mutableListOf()
    val trackAdapter = TrackAdapter(items)

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        btSearchBack.setOnClickListener {
            finish()
        }

        btClearSearch.setOnClickListener {
            edSearch.setText("")
            //скрыть клавиатуру п.4 д/з тема 3/5
            val imm = edSearch.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edSearch.windowToken, 0)
            trackAdapter.items = mutableListOf()
            trackAdapter.notifyDataSetChanged()
        }

        val searchTextWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btClearSearch.isVisible = !s.isNullOrEmpty()
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

        recyclerSearch.layoutManager = LinearLayoutManager(this)
        recyclerSearch.adapter = trackAdapter
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

    fun searchTrack(searchText: String) {
        iTunesAPIService.search(searchText)
            .enqueue(object: Callback<TrackSearchResponse> {
                override fun onResponse(
                    call: Call<TrackSearchResponse>,
                    response: Response<TrackSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackAdapter.items.clear()
                            trackAdapter.items.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                        }
                        else {
                            trackAdapter.items = mutableListOf(PHTrackEmpty())
                            trackAdapter.notifyDataSetChanged()
                        }
                    }
                    else {
                        trackAdapter.items = mutableListOf(PHTrackError())
                        trackAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                    trackAdapter.items = mutableListOf(PHTrackError())
                    trackAdapter.notifyDataSetChanged()
                }
            })
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}