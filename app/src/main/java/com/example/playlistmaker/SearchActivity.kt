package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: TextInputEditText
    private lateinit var searchInputLayout: TextInputLayout
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var placeholderNoResults: View
    private lateinit var placeholderNoConnection: View
    private lateinit var btnRetry: MaterialButton
    private lateinit var historyCard: TextView
    private lateinit var clearHistoryButton: MaterialButton

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.topSearchBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // SharedPreferences
        val prefs = getSharedPreferences("playlist_prefs", Context.MODE_PRIVATE)
        searchHistory = SearchHistory(prefs)

        // ViewModel
        val api = NetworkClient.api
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(api) as T
            }
        })[SearchViewModel::class.java]

        // Views
        searchEditText = findViewById(R.id.search_edit_text)
        searchInputLayout = findViewById(R.id.search_input_layout)
        trackRecyclerView = findViewById(R.id.track_recycler_view)
        placeholderNoResults = findViewById(R.id.placeholder_no_results)
        placeholderNoConnection = findViewById(R.id.placeholder_no_connection)
        btnRetry = findViewById(R.id.btn_retry)
        historyCard = findViewById(R.id.history_card)
        clearHistoryButton = findViewById(R.id.btn_clear_history)

        // Adapter
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            searchHistory.addTrack(track)
            Toast.makeText(this, "Вы выбрали ${track.trackName}", Toast.LENGTH_SHORT).show()
        }
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter

        // LiveData
        viewModel.tracks.observe(this) { tracks ->
            if (tracks.isNotEmpty()) showTracks(tracks)
            else showNoResultsPlaceholder()
        }
        viewModel.error.observe(this) { isError ->
            if (isError) showNoConnectionPlaceholder()
        }

        // Текстовое поле
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchText = s?.toString() ?: ""
                if (viewModel.searchText.isBlank()) showHistory()
                else hideHistory()
            }
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && viewModel.searchText.isBlank()) showHistory()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                hideKeyboard()
                true
            } else false
        }

        // Retry
        btnRetry.setOnClickListener { performSearch() }

        // Очистка поиска
        searchInputLayout.setEndIconOnClickListener {
            searchEditText.text?.clear()
            hideKeyboard()
            trackAdapter.updateTracks(emptyList())
            hidePlaceholders()
            hideHistory()
        }

        // Очистка истории
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            trackAdapter.updateTracks(emptyList())
            hideHistory()
        }

        // Показ истории при старте
        if (viewModel.searchText.isBlank()) showHistory()
    }

    private fun performSearch() {
        val query = viewModel.searchText
        if (query.isNotBlank()) {
            showLoading()
            viewModel.performSearch(query)
        } else {
            Toast.makeText(this, "Введите текст для поиска", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun showLoading() {
        trackAdapter.updateTracks(emptyList())
        trackRecyclerView.visibility = View.VISIBLE
        hidePlaceholders()
        hideHistory()
    }

    private fun showTracks(tracks: List<Track>) {
        trackAdapter.updateTracks(tracks)
        trackRecyclerView.visibility = View.VISIBLE
        hidePlaceholders()
        hideHistory()
    }

    private fun showNoResultsPlaceholder() {
        trackAdapter.updateTracks(emptyList())
        placeholderNoResults.visibility = View.VISIBLE
        trackRecyclerView.visibility = RecyclerView.GONE
        placeholderNoConnection.visibility = View.GONE
        hideHistory()
    }

    private fun showNoConnectionPlaceholder() {
        trackAdapter.updateTracks(emptyList())
        placeholderNoConnection.visibility = View.VISIBLE
        trackRecyclerView.visibility = RecyclerView.GONE
        placeholderNoResults.visibility = View.GONE
        hideHistory()
    }

    private fun hidePlaceholders() {
        placeholderNoResults.visibility = View.GONE
        placeholderNoConnection.visibility = View.GONE
    }

    private fun showHistory() {
        val history = searchHistory.getHistory()
        if (history.isNotEmpty()) {
            trackAdapter.updateTracks(history)
            trackRecyclerView.visibility = View.VISIBLE
            historyCard.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        }
    }

    private fun hideHistory() {
        historyCard.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }
}
