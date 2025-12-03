import android.content.SharedPreferences
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val prefs: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    private val key = "search_history"
    private val maxSize = 10

    override fun getHistory(): List<Track> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > maxSize) history.subList(maxSize, history.size).clear()

        save(history)
    }

    override fun clearHistory() {
        save(emptyList())
    }

    private fun save(tracks: List<Track>) {
        prefs.edit().putString(key, gson.toJson(tracks)).apply()
    }
}
