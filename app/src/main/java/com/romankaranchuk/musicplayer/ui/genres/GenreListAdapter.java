package com.romankaranchuk.musicplayer.ui.genres;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Genre;

/**
 * Created by NotePad.by on 03.12.2016.
 */

public class GenreListAdapter extends ArrayAdapter<Genre> {
    private LayoutInflater layoutInflater;
    private int resource;


    public GenreListAdapter(Context context, int resource, Genre[] list){
        super(context, resource, list);
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        ViewHolder holder;

        View row = convertView;
        if (row == null){
            row = layoutInflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.iconGenre);
            holder.nameView = (TextView) row.findViewById(R.id.nameGenre);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Genre genre = getModel(position);
        holder.imageView.setImageResource(genre.getIconResource());
        holder.nameView.setText(genre.getName());

        return row;
    }

    private class ViewHolder{
        private ImageView imageView;
        private TextView nameView;
    }

    private Genre getModel(int position){
        return (this.getItem(position));
    }
}

