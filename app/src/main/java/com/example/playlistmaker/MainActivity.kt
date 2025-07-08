package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            val button1: Button = findViewById(R.id.button1)
            val button2: Button = findViewById(R.id.button2)
            val button3: Button = findViewById(R.id.button3)

            button1.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Toast.makeText(this@MainActivity, "Нажата кнопка 1", Toast.LENGTH_SHORT).show()
                }
            })

            button2.setOnClickListener {
                Toast.makeText(this, "Нажата кнопка 2", Toast.LENGTH_SHORT).show()
            }

            button3.setOnClickListener {
                Toast.makeText(this, "Нажата кнопка 3", Toast.LENGTH_SHORT).show()
            }
        }

}
