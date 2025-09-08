package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)
    private val artistAndTime: TextView = itemView.findViewById(R.id.artist_and_time)

    fun bind(track: Track) {
        // Название трека
        trackName.text = track.trackName ?: "Без названия"

        // Форматируем время
        val totalMillis = track.trackTimeMillis?.toLongOrNull() ?: 0L
        val minutes = totalMillis / 1000 / 60
        val seconds = totalMillis / 1000 % 60
        val formattedTime = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)

        // Артист + время
        artistAndTime.text = "${track.artistName ?: "Неизвестный артист"} • $formattedTime"

        // Обложка
        Glide.with(itemView)
            .load(track.artworkUrl100 ?: "")
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(2)))
            .into(trackImage)
    }

    private fun dpToPx(dp: Int): Int =
        (dp * itemView.context.resources.displayMetrics.density).toInt()
}