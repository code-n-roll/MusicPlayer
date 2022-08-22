package com.romankaranchuk.musicplayer.data.db;

import android.database.Cursor;

import java.util.ArrayList;

import timber.log.Timber;

public class Util {

    static ArrayList<Integer> logCursor(Cursor c){
        ArrayList<Integer> durations = new ArrayList<>();
        if (c != null){
            if (c.moveToFirst()){
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()){
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                        durations.add(c.getInt(c.getColumnIndex("DURATION")));
                    }
                    Timber.d(str);
                } while (c.moveToNext());
            }
        } else {
            Timber.d("Cursor is null");
        }
        return durations;
    }
}
