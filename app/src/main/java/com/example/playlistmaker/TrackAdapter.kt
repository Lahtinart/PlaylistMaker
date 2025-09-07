package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class TrackAdapter(
    private val tracks: MutableList<Track>,
    private val onTrackClick: ((Track) -> Unit)? = null,
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.track_name)
        private val artist: TextView = view.findViewById(R.id.artist_name)
        private val duration: TextView = view.findViewById(R.id.track_time)
        private val artwork: ImageView = view.findViewById(R.id.track_image)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && position < tracks.size) {
                    onTrackClick?.invoke(tracks[position])
                }
            }
        }

        fun bind(track: Track) {
            name.text = track.trackName ?: "Без названия"
            artist.text = track.artistName ?: "Неизвестный артист"

            val totalMillis = track.trackTimeMillis?.toLongOrNull() ?: 0L
            val totalSeconds = totalMillis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            duration.text = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)

            Glide.with(artwork.context)
                .load(track.artworkUrl100 ?: "")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(2)))
                .into(artwork)
        }

        private fun dpToPx(dp: Int): Int =
            (dp * itemView.context.resources.displayMetrics.density).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
}
