import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(private val tracks: MutableList<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.track_name)
        val artist: TextView = view.findViewById(R.id.artist_name)
        val duration: TextView = view.findViewById(R.id.track_time)
        val artwork: ImageView = view.findViewById(R.id.track_image)
    }

    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]

        holder.name.text = track.trackName
        holder.artist.text = track.artistName

        holder.duration.text = timeFormat.format(track.trackTimeMillis.toLong())

        Glide.with(holder.artwork.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.artwork)
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
}