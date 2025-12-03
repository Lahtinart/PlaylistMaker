package com.example.playlistmaker.features.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.features.search.ui.SearchActivity
import com.example.playlistmaker.features.media.ui.MediaLibraryActivity
import com.example.playlistmaker.features.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)

        button1.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        button2.setOnClickListener {
            startActivity(Intent(this, MediaLibraryActivity::class.java))
        }

        button3.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
