package com.example.playlistmaker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()

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