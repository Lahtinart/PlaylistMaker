package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton: TextView = findViewById(R.id.back_button)
        backButton.setOnClickListener {


            finish()
        }
        val mySwitch = findViewById<SwitchMaterial>(R.id.my_switch)

        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("Switcher", "Включено")
            } else {
                Log.d("Switcher", "Выключено")
            }
        }
    }

}