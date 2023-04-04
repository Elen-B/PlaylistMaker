package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity() {
    private val edSearch: EditText by lazy { findViewById(R.id.edSearch) }
    private val btSearchBack: ImageButton by lazy {findViewById(R.id.btSearchBack)}
    private val btClearSearch: ImageView by lazy {findViewById(R.id.btClearSearch)}
    private var searchText: String? = ""

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
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
            //скрыть клавиатуру п.4 д/з тема 3/5
            val imm = edSearch.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edSearch.windowToken, 0)
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
}