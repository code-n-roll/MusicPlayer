package com.romankaranchuk.musicplayer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Locale;

public class MathUtils {

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

    public static Integer tryParse(String text){
        try{
            return Integer.parseInt(text);
        } catch (NumberFormatException e){
            return -1;
        }
    }
}
