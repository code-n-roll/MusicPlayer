package com.romankaranchuk.musicplayer.ui.tracklist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.utils.MathUtils;

import com.squareup.picasso.Picasso;

/**
 * Created by NotePad.by on 17.10.2016.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Song item);
    }

    private final ArrayList<Song> songs;
    private final OnItemClickListener listener;

    public SongListAdapter(ArrayList<Song> songs, OnItemClickListener listener, Context context){
        this.songs = songs;
        this.listener = listener;
    }

    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.content_songcardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(songs.get(position), listener);
    }


    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView albumCoverView, languageView;
        private TextView nameSongView, nameArtistView, durationView, yearView;
        private View view;
        private boolean firstCreating;
        private Context context;


        public ViewHolder(View view){
            super(view);
            this.view = view;
            albumCoverView = (ImageView) view.findViewById(R.id.iconAlbumCover);
            nameSongView = (TextView) view.findViewById(R.id.nameSong);
            nameArtistView = (TextView) view.findViewById(R.id.nameArtist);
            durationView = (TextView) view.findViewById(R.id.timeSongCardView);
            yearView = (TextView) view.findViewById(R.id.yearSongInfo);
            languageView = (ImageView) view.findViewById(R.id.country_flag);
        }

        public void bind(final Song song, final OnItemClickListener listener) {
            int idDrawable = MathUtils.tryParse(song.getImagePath());
            if (idDrawable == R.drawable.unknown_album_cover){
                Picasso.with(view.getContext()).load(idDrawable).into(albumCoverView);
                albumCoverView.setPadding(40,40,40,40);
            } else {
                Picasso.with(view.getContext()).load(song.getImagePath()).into(albumCoverView);
                albumCoverView.setPadding(0,0,0,0);
            }

            durationView.setText(MathUtils.convertMillisToMin(song.getDuration()));
            nameSongView.setText(song.getTitle());
            nameArtistView.setText(song.getNameArtist());

            languageView.setImageBitmap(null);
            switch(TracklistFragment.getSortBy()){
                case 2:
                    yearView.setText(song.getYear());

                    break;
                case 3:
                    yearView.setText(song.getDate());
                    break;
                case 4:
                    yearView.setText(song.getPath().substring(song.getPath().lastIndexOf(".") + 1));
                    break;
                case 5:
                    yearView.setText("");
                    languageView.setImageResource(view.getResources().getIdentifier(
                            song.getLanguage().toLowerCase(),
                            "drawable",
                            view.getContext().getPackageName()
                    ));
                    break;
                default:
                    yearView.setText("");
                    break;
            }

//            if (firstCreating) {
//                Animation roll_up = AnimationUtils.loadAnimation(view.getContext(), R.anim.songcard_roll_up);
//                roll_up.setStartOffset(songs.indexOf(song) * 1000);
//
//                view.startAnimation(roll_up);
//            }
//            if (songs.indexOf(song) == songs.size()-1){
//                firstCreating = false;
//            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(song);
                }
            });

        }
    }
}
