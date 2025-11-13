package com.example.playlistmaker.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorage(private val prefs: SharedPreferences) {

    private val gson = Gson()
    private val key = "search_history"
    private val maxSize = 10

    fun getHistory(): List<Track> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        // удаляем дубликаты
        history.removeAll { it.trackId == track.trackId }

        // добавляем в начало
        history.add(0, track)

        // ограничиваем до 10
        if (history.size > maxSize) {
            history.subList(maxSize, history.size).clear()
        }

        save(history)
    }

    fun clearHistory() = save(emptyList())

    private fun save(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        prefs.edit().putString(key, json).apply()
    }
}
