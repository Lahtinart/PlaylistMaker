package com.example.playlistmaker.features.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.features.player.ui.AudioPlayerActivity
import com.example.playlistmaker.features.search.data.dto.ParcelizedTrack
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.presentation.SearchViewModel
import com.example.playlistmaker.features.search.presentation.SearchViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var trackAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val SEARCH_DEBOUNCE_DELAY = 700L
    private val CLICK_DEBOUNCE_DELAY = 1000L
    private var isClickAllowed = true
    private val searchRunnable = Runnable { doSearch() }

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
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAdapter()
        initViewModel()
        observeViewModel()
        setupListeners()

        if (viewModel.currentQuery.isBlank()) viewModel.loadHistory()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = binding.topSearchBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupAdapter() {
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            if (clickDebounce()) {
                viewModel.openTrack(track)
                viewModel.addTrackToHistory(track)
            }
        }
        binding.trackRecyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.trackRecyclerView.adapter = trackAdapter
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, SearchViewModelFactory(applicationContext))
            .get(SearchViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(this) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            binding.trackRecyclerView.visibility = if (state.tracks.isNotEmpty()) View.VISIBLE else View.GONE
            binding.clearHistoryInclude.root.visibility = if (state.showClearHistoryButton) View.VISIBLE else View.GONE
            binding.placeholderNoResults.visibility = if (state.noResults) View.VISIBLE else View.GONE
            binding.placeholderNoConnection.visibility = if (state.networkError != null) View.VISIBLE else View.GONE

            // История отображается только если есть история и поле поиска пустое
            binding.historyCard.visibility =
                if (state.tracks.isNotEmpty() && viewModel.currentQuery.isBlank()) View.VISIBLE
                else View.GONE

            if (state.tracks.isNotEmpty()) {
                trackAdapter.updateTracks(state.tracks)
            }
        }

        viewModel.openTrackEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { track ->
                val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra("track", ParcelizedTrack.make(track))
                }
                startActivity(intent)
            }
        }
    }

    private fun setupListeners() {
        binding.searchEditText.afterTextChanged {
            val q = it ?: ""
            viewModel.currentQuery = q

            handler.removeCallbacks(searchRunnable)

            if (q.isBlank()) {
                viewModel.loadHistory()
            } else {
                handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doSearch()
                hideKeyboard()
                true
            } else false
        }

        binding.btnRetry.setOnClickListener { doSearch() }

        binding.searchInputLayout.setEndIconOnClickListener {
            binding.searchEditText.text?.clear()
            hideKeyboard()
            trackAdapter.updateTracks(emptyList())
            binding.trackRecyclerView.visibility = View.GONE
            hidePlaceholders()
            viewModel.currentQuery = ""

            // Скрываем историю и кнопку очистки истории
            binding.historyCard.visibility = View.GONE
            binding.clearHistoryInclude.root.visibility = View.GONE
        }

        binding.clearHistoryInclude.btnClearHistoryItem.setOnClickListener {
            if (clickDebounce()) {
                viewModel.clearHistory()
            }
        }
    }

    private fun doSearch() {
        val q = viewModel.currentQuery
        if (q.isBlank()) {
            Toast.makeText(this, "Введите текст для поиска", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.search(q)
    }

    private fun hidePlaceholders() {
        binding.placeholderNoResults.visibility = View.GONE
        binding.placeholderNoConnection.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}

// Extension
private fun TextInputEditText.afterTextChanged(afterTextChanged: (String?) -> Unit) {
    this.addTextChangedListener(object : android.text.TextWatcher {
        override fun afterTextChanged(s: android.text.Editable?) {
            afterTextChanged.invoke(s?.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}
