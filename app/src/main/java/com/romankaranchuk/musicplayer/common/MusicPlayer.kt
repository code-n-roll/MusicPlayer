package com.romankaranchuk.musicplayer.common

import android.media.MediaPlayer
import java.io.IOException

interface MusicPlayer {
    fun resume()
    fun pause()
    fun prepare(filepath: String)
    fun start(filepath: String)
    fun stop()
    fun repeat()

    fun getCurrentPosition(): Int
    fun getDuration(): Int
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener?)
    fun isPlaying(): Boolean
    fun seekTo(positionInMs: Int)

    var isLooping: Boolean

    val mediaPlayer: MediaPlayer
}

class MusicPlayerImpl(
    override val mediaPlayer: MediaPlayer
) : MusicPlayer {

    override fun resume() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun prepare(filepath: String) {
        val oldLooping = mediaPlayer.isLooping
        mediaPlayer.reset()
        mediaPlayer.isLooping = oldLooping
        try {
            mediaPlayer.setDataSource(filepath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mediaPlayer.prepare()
    }

    override fun start(filepath: String) {
        prepare(filepath)

        mediaPlayer.start()
    }

    override fun stop() {
        mediaPlayer.reset()
    }

    override fun repeat() {

    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getDuration(): Int {
        return mediaPlayer.duration
    }

    override var isLooping: Boolean
        get() {
            return mediaPlayer.isLooping
        }
        set(value) {
            mediaPlayer.isLooping = value
        }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener?) {
        mediaPlayer.setOnCompletionListener(listener)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun seekTo(positionInMs: Int) {
        mediaPlayer.seekTo(positionInMs)
    }
}