package com.romankaranchuk.musicplayer.utils;

import static com.romankaranchuk.musicplayer.presentation.ui.main.MainActivity.path;

import com.romankaranchuk.musicplayer.data.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class JniUtils {
    static {
        System.loadLibrary("native-lib");
    }
    public static native String stringFromJNI();
    public static native long sum(ArrayList<Integer> list);


    public static List<Integer> printAllSongs(List<Song> songs){
        ArrayList<Integer> durations = new ArrayList<>();
        for (Song song : songs){
            durations.add(MusicUtils.extractSongInfo(new File(path,song.getPath()).toString()).duration);
        }
        return durations;
    }

    public static void checkJNI(ArrayList<Integer> durations){
        long result = sum(durations);
        Timber.d("hello JNI"+ stringFromJNI());
        Timber.d("JNI all songs duration"+ Long.toString(result));

        long startTime = System.currentTimeMillis();
        long sum = 0;
        for (int j = 0; j < 10000; j++){
            for (Integer value : durations){
                sum += value;
            }
        }
        Timber.d("Java all songs duration"+ Long.toString(sum));
        Timber.d("Java running time"+ "" + (System.currentTimeMillis() - startTime));
    }
}
