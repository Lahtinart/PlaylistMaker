package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val PREF_KEY_HISTORY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(PREF_KEY_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        // убираем дубликаты
        history.removeAll { it.trackId == track.trackId }

        // добавляем в начало
        history.add(0, track)

        // обрезаем до 10
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }

        saveHistory(history)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(PREF_KEY_HISTORY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(PREF_KEY_HISTORY, json).apply()
    }
}