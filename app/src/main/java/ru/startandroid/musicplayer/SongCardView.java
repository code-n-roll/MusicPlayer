package ru.startandroid.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NotePad.by on 14.10.2016.
 */

public class SongCardView implements Parcelable{
    private int id;
    private String nameSong;
    private String nameArtist;
    private int albumCoverResource;
    private int lyricSong;

    public SongCardView(int id, String nameSong, String nameArtist,
                        int albumCoverResource, int lyricSongResource){
        this.id = id;
        this.nameSong = nameSong;
        this.nameArtist = nameArtist;
        this.albumCoverResource = albumCoverResource;
        this.lyricSong = lyricSongResource;
    }



    public int getLyricSong(){return this.lyricSong;}
    public void setLyricSong(int lyricSong){
        this.lyricSong = lyricSong;
    }
    public String getNameSong(){
        return this.nameSong;
    }

    public void setNameSong(String nameSong){
        this.nameSong = nameSong;
    }

    public String getNameArtist(){
        return nameArtist;
    }

    public void setNameArtist(String nameArtist){
        this.nameArtist = nameArtist;
    }

    public int getAlbumCoverResource(){
        return this.albumCoverResource;
    }

    public void setAlbumCoverResource(int albumCoverResource){
        this.albumCoverResource = albumCoverResource;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.nameSong);
        parcel.writeString(this.nameArtist);
        parcel.writeInt(this.albumCoverResource);
    }

    public SongCardView(Parcel in){
        this.id = in.readInt();
        this.nameSong = in.readString();
        this.nameArtist = in.readString();
        this.albumCoverResource = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public SongCardView createFromParcel(Parcel in){
            return new SongCardView(in);
        }

        public SongCardView[] newArray(int size){
            return new SongCardView[size];
        }
    };
}
