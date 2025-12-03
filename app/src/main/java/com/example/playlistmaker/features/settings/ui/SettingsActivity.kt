package com.example.playlistmaker.features.settings.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.app.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.features.settings.presentation.SettingsState
import com.example.playlistmaker.features.settings.presentation.SettingsViewModel
import com.example.playlistmaker.features.settings.presentation.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupToolbar()
        observeViewModel()
        setupListeners()
    }

    private fun setupViewModel() {
        val factory = SettingsViewModelFactory((application as App).settingsRepository)
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        viewModel.loadTheme()
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            // временно отключаем listener, чтобы не вызвать toggleTheme при setChecked
            binding.mySwitch.setOnCheckedChangeListener(null)
            binding.mySwitch.isChecked = state.isDarkTheme
            binding.mySwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.toggleTheme(isChecked)
                recreate() // чтобы применить тему
            }
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun setupListeners() {
        // Share
        binding.shareItem.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(com.example.playlistmaker.R.string.share_message))
                type = "text/plain"
            }
            startActivity(
                Intent.createChooser(
                    shareIntent,
                    getString(com.example.playlistmaker.R.string.share_chooser_title)
                )
            )
        }

        // Support
        binding.supportItem.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(com.example.playlistmaker.R.string.mail_to)))
                putExtra(Intent.EXTRA_SUBJECT, getString(com.example.playlistmaker.R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(com.example.playlistmaker.R.string.email_text))
            }

            try {
                startActivity(Intent.createChooser(intent, getString(com.example.playlistmaker.R.string.send_email)))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    getString(com.example.playlistmaker.R.string.no_email_client),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // User Agreement
        binding.userAgreementItem.setOnClickListener {
            val url = getString(com.example.playlistmaker.R.string.url_user_agreement)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    getString(com.example.playlistmaker.R.string.no_app_to_open_link),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
