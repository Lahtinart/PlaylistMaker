package com.example.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_theme", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        val mySwitch = findViewById<SwitchMaterial>(R.id.my_switch)
        mySwitch.isChecked = isDarkMode

        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_theme", isChecked).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        val shareTextView = findViewById<MaterialTextView>(R.id.share_item)

        shareTextView.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Хочешь научиться Android-разработке? Попробуй курс от Практикума: https://practicum.yandex.ru/android-developer/")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Поделиться через"))
        }

        val supportTextView = findViewById<MaterialTextView>(R.id.support_item)

        supportTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:lahtinart@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Сообщение разработчикам и разработчицам приложения Playlist Maker")
                putExtra(Intent.EXTRA_TEXT, "Спасибо разработчикам и разработчицам за крутое приложение!")
            }

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Нет почтового клиента на устройстве", Toast.LENGTH_SHORT).show()
            }
        }

        val userAgreementTextView = findViewById<MaterialTextView>(R.id.user_agreement_item)

        userAgreementTextView.setOnClickListener {
            val url = "https://yandex.ru/legal/practicum_offer/ru/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Не найдено приложение для открытия ссылки", Toast.LENGTH_SHORT).show()
            }
        }
    }
}