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
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.presentation.SearchState
import com.example.playlistmaker.features.search.presentation.SearchViewModel
import com.example.playlistmaker.features.search.presentation.SearchViewModelFactory
import com.example.playlistmaker.features.player.ui.AudioPlayerActivity
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
                // ВАЖНО: сначала открыть трек, потом — сохранить
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
        viewModel.state.observe(this) { state ->
            when (state) {
                is SearchState.Idle -> {
                    hideContent()
                    binding.progressBar.visibility = View.GONE
                }

                is SearchState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    hidePlaceholders()
                    binding.trackRecyclerView.visibility = View.GONE
                }

                is SearchState.Content -> {
                    binding.progressBar.visibility = View.GONE
                    hidePlaceholders()
                    trackAdapter.updateTracks(state.tracks)
                    binding.trackRecyclerView.visibility = View.VISIBLE
                }

                is SearchState.NoResults -> {
                    hideContent()
                    binding.progressBar.visibility = View.GONE
                    binding.placeholderNoResults.visibility = View.VISIBLE
                }

                is SearchState.NetworkError -> {
                    hideContent()
                    binding.progressBar.visibility = View.GONE
                    binding.placeholderNoConnection.visibility = View.VISIBLE
                }
            }
        }

        viewModel.showClearHistoryButton.observe(this) { show ->
            binding.clearHistoryInclude.root.visibility =
                if (show) View.VISIBLE else View.GONE
        }

        // обработка открытия трека
        viewModel.openTrackEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { track ->
                val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra("track", track)
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
                // УБРАНО: progressBar.visibility = VISIBLE
                // ViewModel сам включит Loading при реальном поиске
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
        }

        // ————— ДОБАВЛЕН НАСТОЯЩИЙ обработчик очистки истории —————
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

    private fun hideContent() {
        binding.trackRecyclerView.visibility = View.GONE
        hidePlaceholders()
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
