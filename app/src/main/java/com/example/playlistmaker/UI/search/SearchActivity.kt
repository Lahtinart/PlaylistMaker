package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.search1.SearchViewModel
import com.example.playlistmaker.presentation.search1.SearchViewModelFactory
import com.example.playlistmaker.ui.player.AudioPlayerActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
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
    private lateinit var btnClearHistory: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator

    private lateinit var viewModel: SearchViewModel

    private val handler = Handler(Looper.getMainLooper())
    private val SEARCH_DEBOUNCE_DELAY = 2000L
    private val CLICK_DEBOUNCE_DELAY = 1000L
    private var isClickAllowed = true
    private val searchRunnable = Runnable { performSearch() }

    enum class SearchState { LOADING, ERROR, NO_RESULTS, RESULTS }

    private fun clickDebounce(): Boolean {
        val allowed = isClickAllowed
        if (allowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return allowed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initAdapter()
        initViewModel()
        observeViewModel()
        setupListeners()

        if (viewModel.searchText.isBlank()) viewModel.loadHistory()
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.search_edit_text)
        searchInputLayout = findViewById(R.id.search_input_layout)
        trackRecyclerView = findViewById(R.id.track_recycler_view)
        placeholderNoResults = findViewById(R.id.placeholder_no_results)
        placeholderNoConnection = findViewById(R.id.placeholder_no_connection)
        btnRetry = findViewById(R.id.btn_retry)
        historyCard = findViewById(R.id.history_card)
        progressBar = findViewById(R.id.progressBar)

        val clearHistoryContainer: FrameLayout = findViewById(R.id.clear_history_container)
        val clearHistoryView = layoutInflater.inflate(R.layout.item_clear_history, clearHistoryContainer, false)
        clearHistoryContainer.addView(clearHistoryView)
        btnClearHistory = clearHistoryView.findViewById(R.id.btn_clear_history_item)

        val toolbar: Toolbar = findViewById(R.id.topSearchBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initAdapter() {
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
            }
        }
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(applicationContext)
        )[SearchViewModel::class.java]
    }

    private fun observeViewModel() {
        // Отслеживаем список треков
        viewModel.tracks.observe(this) { tracks ->
            if (tracks.isNotEmpty()) {
                showState(SearchState.RESULTS, tracks)
            } else {
                showState(SearchState.NO_RESULTS)
            }
        }

        // Отслеживаем ошибки поиска
        viewModel.error.observe(this) { isError ->
            if (isError) {
                showState(SearchState.ERROR)
            }
        }

        // Можно дополнительно отслеживать индикатор загрузки
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                showState(SearchState.LOADING)
            }
        }
    }

    private fun setupListeners() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchText = s?.toString() ?: ""
                handler.removeCallbacks(searchRunnable)
                if (viewModel.searchText.isBlank()) {
                    viewModel.loadHistory()
                    progressBar.visibility = View.GONE
                } else {
                    hideHistoryCard()
                    showState(SearchState.LOADING)
                    handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
                }
            }
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && viewModel.searchText.isBlank()) viewModel.loadHistory()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                hideKeyboard()
                true
            } else false
        }

        btnRetry.setOnClickListener { performSearch() }

        searchInputLayout.setEndIconOnClickListener {
            searchEditText.text?.clear()
            hideKeyboard()
            trackAdapter.updateTracks(emptyList())
            hidePlaceholders()
            hideHistoryCard()
            progressBar.visibility = View.GONE
        }

        btnClearHistory.setOnClickListener { viewModel.clearHistory() }
    }

    private fun performSearch() {
        val query = viewModel.searchText
        if (query.isNotBlank()) {
            showState(SearchState.LOADING)
            viewModel.search()
        } else {
            Toast.makeText(this, "Введите текст для поиска", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showState(state: SearchState, tracks: List<Track> = emptyList()) {
        when (state) {
            SearchState.LOADING -> {
                progressBar.visibility = View.VISIBLE
                trackRecyclerView.visibility = View.VISIBLE
                hidePlaceholders()
                hideHistoryCard()
                trackAdapter.updateTracks(emptyList())
            }
            SearchState.RESULTS -> {
                progressBar.visibility = View.GONE
                hidePlaceholders()
                hideHistoryCard()
                trackAdapter.updateTracks(tracks)
                trackRecyclerView.visibility = View.VISIBLE
            }
            SearchState.NO_RESULTS -> {
                progressBar.visibility = View.GONE
                hideHistoryCard()
                placeholderNoResults.visibility = View.VISIBLE
                placeholderNoConnection.visibility = View.GONE
                trackAdapter.updateTracks(emptyList())
                trackRecyclerView.visibility = RecyclerView.GONE
            }
            SearchState.ERROR -> {
                progressBar.visibility = View.GONE
                hideHistoryCard()
                placeholderNoConnection.visibility = View.VISIBLE
                placeholderNoResults.visibility = View.GONE
                trackAdapter.updateTracks(emptyList())
                trackRecyclerView.visibility = RecyclerView.GONE
            }
        }
    }

    private fun hidePlaceholders() {
        placeholderNoResults.visibility = View.GONE
        placeholderNoConnection.visibility = View.GONE
    }

    private fun hideHistoryCard() {
        historyCard.visibility = View.GONE
        btnClearHistory.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}
