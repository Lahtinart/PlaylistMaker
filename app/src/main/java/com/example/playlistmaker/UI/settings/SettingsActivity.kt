package com.example.playlistmaker.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.App
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private val settingsRepository by lazy {
        (application as App).settingsRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { finish() }

        // Переключатель темы
        val mySwitch = findViewById<SwitchMaterial>(R.id.my_switch)
        mySwitch.isChecked = settingsRepository.isDarkThemeEnabled()
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsRepository.setDarkThemeEnabled(isChecked)
            // Пересоздаём активити для применения темы
            recreate()
        }

        // Шаринг приложения
        findViewById<MaterialTextView>(R.id.share_item).setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)))
        }

        // Поддержка по email
        findViewById<MaterialTextView>(R.id.support_item).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_to)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            }
            try {
                startActivity(Intent.createChooser(intent, getString(R.string.send_email)))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, getString(R.string.no_email_client), Toast.LENGTH_SHORT).show()
            }
        }

        // Пользовательское соглашение
        findViewById<MaterialTextView>(R.id.user_agreement_item).setOnClickListener {
            val url = getString(R.string.url_user_agreement)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, getString(R.string.no_app_to_open_link), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
