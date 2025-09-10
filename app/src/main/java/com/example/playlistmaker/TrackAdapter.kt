package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val tracks: MutableList<Track>,
    private val onTrackClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackViewHolder>() {

    interface OnTrackClickListener {
        fun onTrackClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        // Привязываем к тегу для обработки клика
        holder.itemView.tag = object : TrackAdapter.OnTrackClickListener {
            override fun onTrackClick(positionTag: Int) {
                if (positionTag in tracks.indices) {
                    onTrackClick?.invoke(tracks[positionTag])
                }
            }
        }
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
}
