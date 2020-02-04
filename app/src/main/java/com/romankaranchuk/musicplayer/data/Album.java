package com.romankaranchuk.musicplayer.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;
import java.util.UUID;



public class Album implements Parcelable {
    private String mId;
    private String mName;
    private String mArtist;
    private String mPath;
    private String mImagePath;

    public Album(String name, String artist, String path, String imagePath) {
        this(UUID.randomUUID().toString(), name, artist, path, imagePath);
    }

    public Album(String id, String name, String artist, String path, String imagePath) {
        mId = id;
        mName = name;
        mArtist = artist;
        mPath = path;
        mImagePath = imagePath;
    }

    private Album(Parcel parcel) {
        mId = parcel.readString();
        mName = parcel.readString();
        mArtist = parcel.readString();
        mPath = parcel.readString();
        mImagePath = parcel.readString();
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getPath() {
        return mPath;
    }

    public String getImagePath() {
        return mImagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mId);
        parcel.writeString(mName);
        parcel.writeString(mArtist);
        parcel.writeString(mPath);
        parcel.writeString(mImagePath);
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (obj.getClass() != Album.class){
            return false;
        }
        final Album album = (Album) obj;

        return Objects.equals(this, obj);
    }
}
