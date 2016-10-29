package ru.startandroid.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by NotePad.by on 17.10.2016.
 */

public class SongCardViewAdapter extends ArrayAdapter<SongCardView> {
    private LayoutInflater layoutInflater;
    private int resource;

    SongCardViewAdapter(Context context, int resource, ArrayList<SongCardView> list){
        super(context, resource, list);
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    private SongCardView getModel(int position){
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

        SongCardView songCardView = getModel(position);


        holder.albumCoverView.setImageResource(songCardView.getAlbumCoverResource());
        String proxyNameSongView;
        if (resource == R.layout.content_songcardview) {
            proxyNameSongView = songCardView.getId()+1 + ". " + songCardView.getNameSong();
        }
        else if (resource == R.layout.content_recently_songs)
            proxyNameSongView = songCardView.getNameSong();
        else
            proxyNameSongView = songCardView.getNameSong();

        holder.nameSongView.setText(proxyNameSongView);
        holder.nameArtistView.setText(songCardView.getNameArtist());

        return row;
    }

    class ViewHolder{
        private ImageView albumCoverView;
        private TextView nameSongView, nameArtistView;
    }
}
