package com.romankaranchuk.musicplayer.presentation.ui.tracklist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.utils.MathUtils
import com.squareup.picasso.Picasso

import java.util.ArrayList

class TrackListAdapter(
        private val mItemClickListener: (Song) -> Unit,
        private val mItemLongClickListener: (Song) -> Unit
) : RecyclerView.Adapter<TrackListAdapter.ViewHolder>() {

    private val mSongs = ArrayList<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_songcardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mSongs[position], mItemClickListener, mItemLongClickListener)
    }

    override fun getItemCount(): Int {
        return mSongs.size
    }

    fun updateSongs(songs: List<Song>) {
        mSongs.clear()
        mSongs.addAll(songs)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val albumCoverView: ImageView
        private val languageView: ImageView
        private val nameSongView: TextView
        private val nameArtistView: TextView
        private val durationView: TextView
        private val yearView: TextView
        private val firstCreating: Boolean = false
        private val context: Context? = null


        init {
            albumCoverView = view.findViewById(R.id.iconAlbumCover)
            nameSongView = view.findViewById(R.id.nameSong)
            nameArtistView = view.findViewById(R.id.nameArtist)
            durationView = view.findViewById(R.id.timeSongCardView)
            yearView = view.findViewById(R.id.yearSongInfo)
            languageView = view.findViewById(R.id.country_flag)
        }

        fun bind(song: Song,
                 itemClickListener: (Song) -> Unit,
                 itemLongClickListener: (Song) -> Unit) {
            val idDrawable = MathUtils.tryParse(song.imagePath)!!
            //            if (idDrawable == R.drawable.unknown_album_cover){
            //                Picasso.with(view.getContext()).load(idDrawable).into(albumCoverView);
            //                albumCoverView.setPadding(40,40,40,40);
            //            } else {
            Picasso.with(view.context).load(song.imagePath).into(albumCoverView)
            albumCoverView.setPadding(0, 0, 0, 0)
            //            }

            durationView.text = MathUtils.convertMillisToMin(song.duration)
            nameSongView.text = song.title
            nameArtistView.text = song.nameArtist

            languageView.setImageBitmap(null)
            when (TrackListFragment.sortBy) {
                "2" -> yearView.text = song.year
                "3" -> yearView.text = song.date
                "4" -> yearView.text = song.path.substring(song.path.lastIndexOf(".") + 1)
                "5" -> {
                    yearView.text = ""
                    languageView.setImageResource(view.resources.getIdentifier(
                            song.language.toLowerCase(),
                            "drawable",
                            view.context.packageName
                    ))
                }
                else -> yearView.text = ""
            }

            //            if (firstCreating) {
            //                Animation roll_up = AnimationUtils.loadAnimation(view.getContext(), R.anim.songcard_roll_up);
            //                roll_up.setStartOffset(mSongs.indexOf(song) * 1000);
            //
            //                view.startAnimation(roll_up);
            //            }
            //            if (mSongs.indexOf(song) == mSongs.size()-1){
            //                firstCreating = false;
            //            }

            view.setOnClickListener { itemClickListener(song) }
            view.setOnLongClickListener {
                itemLongClickListener(song)
                return@setOnLongClickListener true
            }
        }
    }
}
