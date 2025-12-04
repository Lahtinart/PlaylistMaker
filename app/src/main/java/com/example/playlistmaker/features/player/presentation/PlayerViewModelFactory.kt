package com.example.playlistmaker.features.player.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.features.player.data.interactor.PlayerInteractorImpl

class PlayerViewModelFactory(
    private val context: Context // not strictly needed here, but keep signature consistent
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            val interactor = PlayerInteractorImpl()
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(interactor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
