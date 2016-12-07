package com.romankaranchuk.musicplayer.ui.tracklist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.data.source.MusicDataSource;
import com.romankaranchuk.musicplayer.data.source.MusicRepository;
import com.romankaranchuk.musicplayer.data.source.local.MusicLocalDataSource;
import com.romankaranchuk.musicplayer.utils.MathUtils;
import com.romankaranchuk.musicplayer.utils.SearchLyricUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by NotePad.by on 17.10.2016.
 */

public class SongListAdapter extends ArrayAdapter<Song> {
    private LayoutInflater layoutInflater;
    private int resource;



    public SongListAdapter(Context context, int resource, LinkedList<Song> list){
        super(context, resource, list);
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;

    }

    public SongListAdapter(Context context, int resource, ArrayList<Song> list){
        super(context, resource, list);
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    private Song getModel(int position){
        return (this.getItem(position));
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        ViewHolder holder;
        View row = convertView;
        if (row == null){
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



        Picasso.with(getContext()).load(song.getImagePath()).into(holder.albumCoverView);

        holder.durationView.setText(MathUtils.convertMillisToMin(song.getDuration()));
        holder.nameSongView.setText(song.getTitle());
        holder.nameArtistView.setText(song.getNameArtist());
        if (TracklistFragment.getSortBy() == 2) {
            holder.yearView.setText(song.getYear());
            holder.languageView.setImageBitmap(null);
        } else if (TracklistFragment.getSortBy() == 3) {
            holder.yearView.setText(song.getDate());
            holder.languageView.setImageBitmap(null);
        }else if (TracklistFragment.getSortBy() == 4) {
            holder.yearView.setText(song.getPath().substring(song.getPath().lastIndexOf(".")+1));
            holder.languageView.setImageBitmap(null);
        }  else if (TracklistFragment.getSortBy() == 5) {
            holder.yearView.setText("");
            holder.languageView.setImageResource(
                    getContext().getResources().getIdentifier(
                            song.getLanguage().toLowerCase(), "drawable", getContext().getPackageName()));
        }  else {
            holder.yearView.setText("");
            holder.languageView.setImageBitmap(null);
        }
        return row;
    }

    private class ViewHolder{
        private ImageView albumCoverView, languageView;
        private TextView nameSongView, nameArtistView, durationView, yearView;
    }
}
