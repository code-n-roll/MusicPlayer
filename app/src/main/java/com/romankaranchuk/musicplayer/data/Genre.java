package com.romankaranchuk.musicplayer.data;

/**
 * Created by NotePad.by on 14.10.2016.
 */

public class Genre {
    private String name;
    private int iconResource;

    public Genre(String name, int iconResource){
        this.name = name;
        this.iconResource = iconResource;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public int getIconResource(){
        return iconResource;
    }
}
