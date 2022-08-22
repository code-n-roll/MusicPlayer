package com.romankaranchuk.musicplayer.data.api

import com.romankaranchuk.musicplayer.BuildConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject

interface IMusixmatchApi {
    fun getUrlAlbumCover(artist: String, song: String): String?
    fun getLyrics(artist: String, song: String): String?
    fun getLanguage(artist: String, song: String): String?
}

class MusixmatchApi @Inject constructor() : IMusixmatchApi {

    companion object {
        private val MUSIXMATCH_API_KEY_PROD = BuildConfig.MUSIXMATCH_API_KEY_PROD
    }

    override fun getUrlAlbumCover(artist: String, song: String): String? {
        val query = ("http://api.musixmatch.com/ws/1.1/matcher.track.get?"
                + "q_artist="
                + artist.replace(" ", "%20").lowercase(Locale.getDefault())
                + "&q_track="
                + song.replace(" ", "%20").lowercase(Locale.getDefault())
                + "&apikey=$MUSIXMATCH_API_KEY_PROD"
                + "&format=xml")
        var searchResult = ""
        try {
            var doc = Jsoup.connect(query).get()
            var parts = doc.select("track_share_url")
            if (parts.size > 0) doc = Jsoup.connect(parts.text()).get()
            doc.outputSettings(Document.OutputSettings().prettyPrint(false))
            parts = doc.select("meta[content$=.jpg]")
            if (parts.size > 0) searchResult = parts.first().attr("content")
            Timber.d(searchResult)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return searchResult
    }

    override fun getLyrics(artist: String, song: String): String? {
        // construct the REST query URL
        val query = ("http://api.musixmatch.com/ws/1.1/matcher.track.get?"
                + "q_artist="
                + artist.replace(" ", "%20").lowercase(Locale.getDefault())
                + "&q_track="
                + song.replace(" ", "%20").lowercase(Locale.getDefault())
                + "&apikey=$MUSIXMATCH_API_KEY_PROD"
                + "&format=xml")

        // open the URL and get a stream to read from
        var searchResult = "" //getHtmlByQuery(query);
        try {
            var doc = Jsoup.connect(query).get()
            var parts = doc.select("track_share_url")
            if (parts.size > 0) doc = Jsoup.connect(parts.text()).get()
            doc.outputSettings(Document.OutputSettings().prettyPrint(false))
            parts = doc.select(".mxm-lyrics__content")
            searchResult = parts.html()
            Timber.d(searchResult)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return searchResult
    }

    override fun getLanguage(artist: String, song: String): String? {
        // construct the REST query URL
        val query = ("http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?" +
                "q_artist="
                + artist
                + "&q_track="
                + song
                + "&apikey=$MUSIXMATCH_API_KEY_PROD"
                + "&format=xml")
        // open the URL and get a stream to read from
        var searchResult = "" //getHtmlByQuery(query);
        try {
            val doc = Jsoup.connect(query).get()
            val parts = doc.select("lyrics_language_description")
            searchResult = parts.text()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return searchResult
    }
}