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
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                (itemView.tag as? TrackAdapter.OnTrackClickListener)?.onTrackClick(position)
            }
        }
    }

    fun bind(track: Track) {
        // Название трека
        trackName.text = track.trackName ?: "Без названия"

        // Форматируем время
        val totalMillis = track.trackTimeMillis?.toLongOrNull() ?: 0L
        val minutes = totalMillis / 1000 / 60
        val seconds = totalMillis / 1000 % 60
        val formattedTime = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)

        // Артист и время
        artistName.text = track.artistName ?: "Неизвестный артист"
        trackTime.text = "• $formattedTime"

        // Обложка трека
        Glide.with(trackImage.context)
            .load(track.artworkUrl100 ?: "")
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(2)))
            .into(trackImage)
    }

    private fun dpToPx(dp: Int): Int =
        (dp * itemView.context.resources.displayMetrics.density).toInt()
}
