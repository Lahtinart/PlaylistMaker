package com.example.playlistmaker

import TrackAdapter
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: TextInputEditText
    private lateinit var searchInputLayout: TextInputLayout
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var placeholderNoResults: LinearLayout
    private lateinit var placeholderNoConnection: LinearLayout
    private lateinit var retryButton: Button

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar: Toolbar = findViewById(R.id.topSearchBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val api = NetworkClient.api

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(api) as T
            }
        })[SearchViewModel::class.java]

        searchEditText = findViewById(R.id.search_edit_text)
        searchInputLayout = findViewById(R.id.search_input_layout)
        trackRecyclerView = findViewById(R.id.track_recycler_view)
        placeholderNoResults = findViewById(R.id.placeholder_no_results)
        placeholderNoConnection = findViewById(R.id.placeholder_no_connection)
        retryButton = findViewById(R.id.btn_retry)

        trackAdapter = TrackAdapter(mutableListOf())
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter

        // Обновление списка по LiveData
        viewModel.tracks.observe(this) { tracks ->
            if (tracks.isNotEmpty()) {
                trackAdapter.updateTracks(tracks)
                showRecycler()
            } else {
                showNoResultsPlaceholder()
            }
        }

        viewModel.error.observe(this) { isError ->
            if (isError) {
                showNoConnectionPlaceholder()
            }
        }

        // Слушатель текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchText = s?.toString() ?: ""
            }
        })

        // Поиск по кнопке DONE
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                hideKeyboard()
                true
            } else false
        }

        retryButton.setOnClickListener { performSearch() }

        // Восстановление текста
        searchEditText.setText(viewModel.searchText)
        searchEditText.setSelection(viewModel.searchText.length)

        // 🔑 Обработка крестика (очистка поля)
        searchInputLayout.setEndIconOnClickListener {
            searchEditText.text?.clear()
            hideKeyboard()

            // Скрыть список и плейсхолдеры
            trackAdapter.updateTracks(emptyList())
            trackRecyclerView.visibility = RecyclerView.GONE
            placeholderNoResults.visibility = LinearLayout.GONE
            placeholderNoConnection.visibility = LinearLayout.GONE
        }
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
        trackRecyclerView.visibility = RecyclerView.VISIBLE
        placeholderNoResults.visibility = LinearLayout.GONE
        placeholderNoConnection.visibility = LinearLayout.GONE
    }

    private fun showRecycler() {
        trackRecyclerView.visibility = RecyclerView.VISIBLE
        placeholderNoResults.visibility = LinearLayout.GONE
        placeholderNoConnection.visibility = LinearLayout.GONE
    }

    private fun showNoResultsPlaceholder() {
        trackAdapter.updateTracks(emptyList())
        trackRecyclerView.visibility = RecyclerView.GONE
        placeholderNoResults.visibility = LinearLayout.VISIBLE
        placeholderNoConnection.visibility = LinearLayout.GONE
    }

    private fun showNoConnectionPlaceholder() {
        trackAdapter.updateTracks(emptyList())
        trackRecyclerView.visibility = RecyclerView.GONE
        placeholderNoResults.visibility = LinearLayout.GONE
        placeholderNoConnection.visibility = LinearLayout.VISIBLE
    }
}