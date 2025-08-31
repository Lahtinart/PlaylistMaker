package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import java.util.Locale

class TrackAdapter(
    private val tracks: MutableList<Track>,
    private val onTrackClick: ((Track) -> Unit)? = null,
    private val onClearHistoryClick: (() -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_TRACK = 0
        private const val TYPE_CLEAR_BUTTON = 1
    }

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
            name.text = track.trackName
            artist.text = track.artistName

            val totalMillis = track.trackTimeMillis.toLongOrNull() ?: 0L
            val totalSeconds = totalMillis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            duration.text = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)

            Glide.with(artwork.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(2)))
                .into(artwork)
        }

        private fun dpToPx(dp: Int): Int =
            (dp * itemView.context.resources.displayMetrics.density).toInt()
    }

    inner class ClearButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button: MaterialButton = view.findViewById(R.id.btn_clear_history_item)
        fun bind() {
            button.setOnClickListener { onClearHistoryClick?.invoke() }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (position < tracks.size) TYPE_TRACK else TYPE_CLEAR_BUTTON

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_TRACK) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_track, parent, false)
            TrackViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_clear_history, parent, false)
            ClearButtonViewHolder(view)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TrackViewHolder) holder.bind(tracks[position])
        else if (holder is ClearButtonViewHolder) holder.bind()
    }

    override fun getItemCount(): Int = tracks.size + if (tracks.isNotEmpty()) 1 else 0

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
}