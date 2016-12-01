package com.romankaranchuk.musicplayer.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.romankaranchuk.musicplayer.ui.tracklist.TracklistActivity;
import com.romankaranchuk.musicplayer.utils.MusicUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by NotePad.by on 14.10.2016.
 */

public class Song implements Parcelable{
    private String mId;
    private String mName;
    private String mPath;

    private String mImagePath;
    private int mDuration;
    private String mAlbumId;

    private String mLyricSong;


    public Song(String name, String path, String imagePath,
                int duration, String albumId, String lyricSong){
        this(UUID.randomUUID().toString(), name, path,
                imagePath, duration, albumId, lyricSong);
    }

    public Song(String id, String name, String path,
                String imagePath, int duration, String albumId,
                String lyricSong){
        mId = id;
        mName = name;
        mPath = path;
        mImagePath = imagePath;
        mDuration = duration;
        mAlbumId = albumId;
        mLyricSong = lyricSong;
    }

    public void setImagePath(String imagePath){
        this.mImagePath = imagePath;
    }

    public String getId(){ return this.mId;}

    public String getPath(){
        return this.mPath;
    }

    public String getLyricSong(){
        return this.mLyricSong;
    }

    public void setLyricSong(String mLyricSong){
        this.mLyricSong = mLyricSong;
    }

    public String getName(){
        return this.mName;
    }

    public String getTitle(){
        return MusicUtils.extractSongInfo((new File(TracklistActivity.path,this.getPath())).getPath()).title;
    }

    public String getNameArtist(){
        return MusicUtils.extractSongInfo((new File(TracklistActivity.path,this.getPath())).getPath()).artist;
    }

    public void setName(String mName){
        this.mName = mName;
    }

    public String getImagePath(){
        return this.mImagePath;
    }

    public int getDuration(){
        return this.mDuration;
    }

    public String getAlbumId(){
        return this.mAlbumId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mId);
        parcel.writeString(this.mName);
        parcel.writeString(this.mPath);
        parcel.writeString(this.mImagePath);
        parcel.writeString(this.mLyricSong);
    }

    private Song(Parcel parcel){
        mId = parcel.readString();
        mName = parcel.readString();
        mPath = parcel.readString();
        mImagePath = parcel.readString();
        mLyricSong = parcel.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Song createFromParcel(Parcel in){
            return new Song(in);
        }

        public Song[] newArray(int size){
            return new Song[size];
        }
    };
}
