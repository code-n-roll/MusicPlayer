package com.romankaranchuk.musicplayer.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.romankaranchuk.musicplayer.data.source.MusicDataSource;
import com.romankaranchuk.musicplayer.data.source.MusicRepository;
import com.romankaranchuk.musicplayer.data.source.local.MusicLocalDataSource;
import com.romankaranchuk.musicplayer.utils.MusicUtils;

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

    private String mLyricsSong;
    private String mYear;
    private String mDate;
    private String mLanguage;

    public Song(String name, String path, String imagePath,
                int duration, String albumId, String lyricsSong,
                String year, String dateModified, String language){
        this(UUID.randomUUID().toString(), name, path,
                imagePath, duration, albumId, lyricsSong, year, dateModified, language);
    }

    public Song(String id, String name, String path,
                String imagePath, int duration, String albumId,
                String lyricsSong, String year, String dateModified, String language){
        mId = id;
        mName = name;
        mPath = path;
        mImagePath = imagePath;
        mDuration = duration;
        mAlbumId = albumId;
        mLyricsSong = lyricsSong;
        mYear = year;
        mDate = dateModified;
        mLanguage = language;
    }

    public String getId(){ return this.mId;}

    public String getPath(){
        return this.mPath;
    }

    public String getLyricsSong(){
        return this.mLyricsSong;
    }

    public String getName(){
        return this.mName;
    }

    public String getTitle(){
        return MusicUtils.extractSongInfo(this.getPath()).title;
    }

    public String getNameArtist(){
        return MusicUtils.extractSongInfo(this.getPath()).artist;
    }

    public String getYear(){
        return mYear;
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

    public String getDate(){
        return this.mDate;
    }

    public String getLanguage(){
        return this.mLanguage;
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
        parcel.writeString(this.mLyricsSong);
        parcel.writeInt(this.mDuration);
        parcel.writeString(this.mYear);
        parcel.writeString(this.mAlbumId);
        parcel.writeString(this.mDate);
        parcel.writeString(this.mLanguage);
    }

    private Song(Parcel parcel){
        mId = parcel.readString();
        mName = parcel.readString();
        mPath = parcel.readString();
        mImagePath = parcel.readString();
        mLyricsSong = parcel.readString();
        mDuration = parcel.readInt();
        mYear = parcel.readString();
        mAlbumId = parcel.readString();
        mDate = parcel.readString();
        mLanguage = parcel.readString();
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
