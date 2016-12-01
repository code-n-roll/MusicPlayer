package com.romankaranchuk.musicplayer.ui.tracklist;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

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

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        View row = convertView;
        if (row == null){
            row = layoutInflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.albumCoverView = (ImageView) row.findViewById(R.id.iconAlbumCover);
            holder.nameSongView = (TextView) row.findViewById(R.id.nameSong);
            holder.nameArtistView = (TextView) row.findViewById(R.id.nameArtist);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Song song = getModel(position);


        Picasso.with(getContext()).load(song.getImagePath()).into(holder.albumCoverView);
//        holder.albumCoverView.setImageResource(Integer.parseInt(song.getImagePath()));
        String proxyNameSongView;
        if (resource == R.layout.content_songcardview) {
            proxyNameSongView = Integer.parseInt(song.getId())+1 + ". " + song.getTitle();
        }
        else if (resource == R.layout.content_recently_songs)
            proxyNameSongView = song.getTitle();
        else
            proxyNameSongView = song.getTitle();

        holder.nameSongView.setText(proxyNameSongView);
        holder.nameArtistView.setText(song.getNameArtist());

        return row;
    }

    class ViewHolder{
        private ImageView albumCoverView;
        private TextView nameSongView, nameArtistView;
    }
}
