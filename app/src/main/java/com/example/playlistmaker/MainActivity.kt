package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poster)

        val image = findViewById<ImageView>(R.id.poster)

        val imageClickListener = View.OnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на картинку!", Toast.LENGTH_SHORT).show()
        }

        image.setOnClickListener(imageClickListener)
    }
}
