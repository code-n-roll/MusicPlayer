package com.romankaranchuk.musicplayer.ui.tracklist;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
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

public class SongListAdapter extends ArrayAdapter<Song> {
    private LayoutInflater layoutInflater;
    private int resource;
    private boolean firstCreating = true;


    public SongListAdapter(Context context, int resource, ArrayList<Song> list){
        super(context, resource, list);
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    private Song getModel(int position){
        return (this.getItem(position));
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;
        if (row == null) {
            row = layoutInflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.albumCoverView = (ImageView) row.findViewById(R.id.iconAlbumCover);
            holder.nameSongView = (TextView) row.findViewById(R.id.nameSong);
            holder.nameArtistView = (TextView) row.findViewById(R.id.nameArtist);
            holder.durationView = (TextView) row.findViewById(R.id.timeSongCardView);
            holder.yearView = (TextView) row.findViewById(R.id.yearSongInfo);
            holder.languageView = (ImageView) row.findViewById(R.id.country_flag);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Song song = getModel(position);

        int idDrawable = MathUtils.tryParse(song.getImagePath());
        if (idDrawable == R.drawable.unknown_album_cover){
            Picasso.with(getContext()).load(idDrawable).into(holder.albumCoverView);
            holder.albumCoverView.setPadding(40,40,40,40);
        } else {
            Picasso.with(getContext()).load(song.getImagePath()).into(holder.albumCoverView);
            holder.albumCoverView.setPadding(0,0,0,0);
        }

        holder.durationView.setText(MathUtils.convertMillisToMin(song.getDuration()));
        holder.nameSongView.setText(song.getTitle());
        holder.nameArtistView.setText(song.getNameArtist());
        if (TracklistFragment.getSortBy() == 2) {
            holder.yearView.setText(song.getYear());
            holder.languageView.setImageBitmap(null);
        } else if (TracklistFragment.getSortBy() == 3) {
            holder.yearView.setText(song.getDate());
            holder.languageView.setImageBitmap(null);
        } else if (TracklistFragment.getSortBy() == 4) {
            holder.yearView.setText(song.getPath().substring(song.getPath().lastIndexOf(".") + 1));
            holder.languageView.setImageBitmap(null);
        } else if (TracklistFragment.getSortBy() == 5) {
            holder.yearView.setText("");
            holder.languageView.setImageResource(
                    getContext().getResources().getIdentifier(
                            song.getLanguage().toLowerCase(), "drawable", getContext().getPackageName()));
        } else {
            holder.yearView.setText("");
            holder.languageView.setImageBitmap(null);
        }


        if (firstCreating) {
            Animation roll_up = AnimationUtils.loadAnimation(getContext(), R.anim.songcard_roll_up);
            roll_up.setStartOffset(position * 50);

            row.startAnimation(roll_up);
        }
        if (position == getCount()-1){
            firstCreating = false;
        }

        return row;
    }

    private class ViewHolder{
        private ImageView albumCoverView, languageView;
        private TextView nameSongView, nameArtistView, durationView, yearView;
    }
}
