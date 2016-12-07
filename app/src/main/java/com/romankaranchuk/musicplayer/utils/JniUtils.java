package com.romankaranchuk.musicplayer.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import com.romankaranchuk.musicplayer.data.Song;
import static com.romankaranchuk.musicplayer.ui.tracklist.TracklistActivity.path;

/**
 * Created by NotePad.by on 03.12.2016.
 */

public class JniUtils {
    static {
        System.loadLibrary("native-lib");
    }
    public static native String stringFromJNI();
    public static native long sum(ArrayList<Integer> list);


    public static ArrayList<Integer> printAllSongs(ArrayList<Song> songs){
        ArrayList<Integer> durations = new ArrayList<>();
        for (Song song : songs){
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            durations.add(MusicUtils.extractSongInfo(new File(path,song.getPath()).toString()).duration);
        }
        return durations;
    }

    public static void checkJNI(ArrayList<Integer> durations){
        long result = sum(durations);
        Log.d("hello JNI", stringFromJNI());
        Log.d("JNI all songs duration", Long.toString(result));

        long startTime = System.currentTimeMillis();
        long sum = 0;
        for (int j = 0; j < 10000; j++){
            for (Integer value : durations){
                sum += value;
            }
        }
        Log.d("Java all songs duration", Long.toString(sum));
        Log.d("Java running time", "" + (System.currentTimeMillis() - startTime));
    }
}
