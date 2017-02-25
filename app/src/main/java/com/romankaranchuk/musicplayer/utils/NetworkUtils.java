package com.romankaranchuk.musicplayer.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * Created by NotePad.by on 05.12.2016.
 */

public final class NetworkUtils {

//    http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?
//        q_track=stressed%20out&
//        q_artist=twenty%20one%20pilots&
//        apikey=&format=xml
    //http://api.chartlyrics.com/apiv1.asmx/SearchLyricDirect?artist=michael%20jackson&song=bad
    public static String getUrlAlbumCover(String artist, String song){
        String query = "http://api.musixmatch.com/ws/1.1/matcher.track.get?"
                + "q_artist="
                + artist.replace(" ", "%20").toLowerCase()
                + "&q_track="
                + song.replace(" ", "%20").toLowerCase()
                + "&apikey=53f81a46c21c1a24961ef593d74dd870"
                + "&format=xml";
        String searchResult = "";
        try {
            Document doc = Jsoup.connect(query).get();
            Elements parts = doc.select("track_share_url");
            if (parts.size() > 0)
                doc = Jsoup.connect(parts.text()).get();
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
            parts = doc.select("meta[content$=.jpg]");
            if (parts.size() > 0)
                searchResult = parts.first().attr("content");
            Log.d("NETWORK LOG", searchResult);
        } catch (IOException e){
            e.printStackTrace();
        }
        return searchResult;
    }
    public static String getLyrics(String artist, String song) {
        // construct the REST query URL
        String query = "http://api.musixmatch.com/ws/1.1/matcher.track.get?"
                + "q_artist="
                + artist.replace(" ", "%20").toLowerCase()
                + "&q_track="
                + song.replace(" ", "%20").toLowerCase()
                + "&apikey=53f81a46c21c1a24961ef593d74dd870"
                + "&format=xml";

        // open the URL and get a stream to read from
        String searchResult = "";//getHtmlByQuery(query);
        try {
            Document doc = Jsoup.connect(query).get();
            Elements parts = doc.select("track_share_url");
            if (parts.size() > 0)
                doc = Jsoup.connect(parts.text()).get();
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
            parts = doc.select(".mxm-lyrics__content");
            searchResult = parts.html();
            Log.d("NETWORK LOG", searchResult);
        } catch (IOException e){
            e.printStackTrace();
        }
        return searchResult;


//        int beginLyrics = searchResult.indexOf("<track_share_url>")+17,
//                endLyrics = searchResult.indexOf("</track_share_url>");

//        try {
//            query = searchResult.substring(beginLyrics, endLyrics);

//            searchResult = getHtmlByQuery(query);
//            int beginStuff = searchResult.indexOf("mxm-lyrics__content")+21,
//                    endStuff = searchResult.indexOf("mxm-lyrics__copyright")-10;

//            searchResult = "<"+searchResult.substring(beginStuff, endStuff);
//            Log.d("ORIGIN_VER",searchResult);

//            beginStuff = searchResult.indexOf("mxm-lyrics__quote-container")-12;
//            endStuff = searchResult.lastIndexOf("data-reactid");
//
//            if (beginStuff > -1 && endStuff > -1) {
//                String firstPart = searchResult.substring(0, beginStuff) + "\n\n<";
//                String lastPart = searchResult.substring(endStuff, searchResult.length());
//                searchResult = firstPart.concat(lastPart);
//            }
//            String temp = "";
//            try {
//                Document doc = Jsoup.connect(query).get();
//                doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
//                Elements parts = doc.select(".mxm-lyrics__content");
//                temp = parts.html();
//                Log.d("log", temp);

//            } catch (IOException e){
//                e.printStackTrace();
//            }
//            Log.d("EDIT_VER_1", searchResult);

//            searchResult = searchResult.
//                    replaceAll("<[^>]*>","").
//                    replaceAll("\\{[^\\}]*\\}","").
//                    replaceAll("[\\s]*googletag\\.cmd\\.push\\(function\\(\\) \\);\n","\n");
//            Log.d("EDIT_VER_2",searchResult);

//        } catch (StringIndexOutOfBoundsException e){
//            e.printStackTrace();
//            searchResult = "";
//        }
//        return searchResult;
    }

    public static String getLanguage(String artist, String song) {
        // construct the REST query URL
        String query = "http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?" +
                "q_artist="
                + artist
                + "&q_track="
                + song
                + "&apikey=53f81a46c21c1a24961ef593d74dd870"
                + "&format=xml";
        // open the URL and get a stream to read from
        String searchResult = "";//getHtmlByQuery(query);
        try {
            Document doc = Jsoup.connect(query).get();
            Elements parts = doc.select("lyrics_language_description");
            searchResult = parts.text();
//        int begin = searchResult.indexOf("<lyrics_language_description>") + 29,
//                end = searchResult.indexOf("</lyrics_language_description>");
//            searchResult = searchResult.substring(begin, end);
//        } catch (StringIndexOutOfBoundsException e){
//            e.printStackTrace();
//            return "";
        } catch (IOException e){
            e.printStackTrace();
        }
        return searchResult;
    }

    public static String getHtmlByQuery(String query){
        StringBuilder buf = new StringBuilder();
        try {
            URL url = new URL(query);
            InputStream is = url.openStream();
            // get the text from the stream as lines
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                buf.append(s).append('\n');
                Log.d("NETWORK LOG", s);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        // return the lines
        return buf.toString();
    }


}



/*public static String getLyrics(String artist, String song)
    {
        // construct the REST query URL
        String query = "http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?"+
                "q_artist="
                + artist
                + "&q_track="
                + song
                + "&apikey=your_api"
                + "&format=xml";
        // open the URL and get a stream to read from
        StringBuilder buf = new StringBuilder();
        try {
            URL url = new URL(query);
            InputStream is = url.openStream();
            // get the text from the stream as lines
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String s;
            while ((s = reader.readLine()) != null)
                buf.append(s).append('\n');
        } catch (IOException e){
            e.printStackTrace();
        }
        // return the lines
        return buf.toString();
    }*/