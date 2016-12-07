package com.romankaranchuk.musicplayer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.romankaranchuk.musicplayer.data.Song;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by NotePad.by on 03.12.2016.
 */

public class MathUtils {
    static final String BY_NAME = "0",
            BY_DURATION = "1",
            BY_YEAR = "2",
            BY_DATE_MODIFIED = "3",
            BY_FORMAT = "4",
            BY_LANGUAGE = "5";
    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px/ metrics.density;
        return (int)dp;
    }

    public static String convertMillisToMin(int timeMillis) {
        double minutes = (double)  timeMillis/ 60000;
        double seconds = (minutes - (int) minutes) * 60;
        return String.format(Locale.getDefault(), "%d", (int) minutes) + ":" +
                String.format(Locale.getDefault(), "%02.0f", seconds);
    }

    public static Comparator<Song> getComparator(int sortBy){
        switch (String.valueOf(sortBy)){
            case BY_NAME:
                return new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getName().compareTo(song2.getName());
                    }
                };
            case BY_DURATION:
                return new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getDuration() - song2.getDuration();
                    }
                };
            case BY_YEAR:
                return new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        if (song1.getYear() != null && song2.getYear() != null) {
                            return song1.getYear().compareTo(song2.getYear());
                        } else {
                            return 0;
                        }
                    }
                };
            case BY_DATE_MODIFIED:
                return new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
//                        return song1.getDateModified() - song2.getDateModified();
                        return 0;
                    }
                };
            case BY_FORMAT:
                return new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        int pos1 = song1.getPath().lastIndexOf(".");
                        int pos2 = song2.getPath().lastIndexOf(".");
                        String format1 = song1.getPath().substring(pos1+1);
                        String format2 = song2.getPath().substring(pos2+1);
                        return format1.compareTo(format2);
                    }
                };
            case BY_LANGUAGE:
                return new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        if (song1.getLanguage() != null && song2.getLanguage() != null){
                            return song1.getLanguage().compareTo(song2.getLanguage());
                        } else {
                            return 0;
                        }
                    }
                };
            default:
                return new Comparator<Song>() {
                    @Override
                    public int compare(Song song1, Song song2) {
                        return song1.getName().compareTo(song2.getName());
                    }
                };
        }
    }
}
