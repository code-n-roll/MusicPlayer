package com.romankaranchuk.musicplayer.presentation.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.os.Parcelable
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.romankaranchuk.musicplayer.R
import com.romankaranchuk.musicplayer.data.Song
import com.romankaranchuk.musicplayer.presentation.ui.main.MainActivity
import com.romankaranchuk.musicplayer.common.MusicPlayer
import com.romankaranchuk.musicplayer.presentation.ui.player.PlayerFragment
import com.romankaranchuk.musicplayer.utils.MathUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import java.io.File
import java.io.IOException
import javax.inject.Inject

class PlayerServiceViewModel @Inject constructor(
    private val nm: NotificationManager,
    private val wallpaperManager: WallpaperManager,
    private val picasso: Picasso,
    @Deprecated("make private after refactoring") val musicPlayer: MusicPlayer,
    @Deprecated("remove after refactoring") private val context: Context
) : ViewModel() {

    private lateinit var remoteViewsContent: RemoteViews
    private lateinit var remoteViewsBigContent: RemoteViews
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val TAG_CANCEL_BIG = "cancelNotifPlayerReceiver"
    private val TAG_FORWARD = "forwardNotifPlayerReceiver"
    private val TAG_PLAY = "playNotifPlayerReceiver"
    private val TAG_BACKWARD = "backwardNotifPlayerReceiver"
    private val TAG_PLAY_BUT_PS_TO_F_BR = "playButtonFromPStoFragmentBR"
    private val TAG_FORWARD_BUT_PS_TO_F_BR = "forwardButtonFromPStoFragmentBR"
    private val TAG_BACKWARD_BUT_PS_TO_F_BR = "backwardButtonFromPStoFragmentBR"
    private val TAG_RESTORE_FPF_PS_TO_F_BR = "restoreFpfFromPStoF"

    private val oldWallpaper: Bitmap? = null
    private var cancelNotifPlayerReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, "notification big was deleted", Toast.LENGTH_SHORT).show()
            if (isPlaying) playNotifPlayerReceiver?.onReceive(context, intent)
            nm.cancel(1)
            _state.value = State.OnCancel
        }
    }
    private var playNotifPlayerReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, "play notification big was clicked", Toast.LENGTH_SHORT)
                .show()
            if (intent.getParcelableExtra<Parcelable?>("currentSong") != null) {
                currentSong = intent.getParcelableExtra<Song>("currentSong")
                isPlaying = intent.getBooleanExtra("isPlaying", false)
            }
            changePlayButtonImage(remoteViewsContent, remoteViewsBigContent)
            if (intent.getStringExtra("fpfCall") == null) {
                handlePlayerLogic()
                context.sendBroadcast(Intent(TAG_PLAY_BUT_PS_TO_F_BR))
            }
        }
    }
    private var forwardNotifPlayerReceiver: BroadcastReceiver? = null
    private var backwardNotifPlayerReceiver: BroadcastReceiver? = null

    private var isPlaying = false
    private val path: File? = null
    private var smallIcon = 0
    private var currentSong: Song? = null
    private var oldAlbumCoverResource: String? = null

    private var mBuilder: NotificationCompat.Builder? = null

    fun UpdateLockscreen() {
        if (currentSong != null && currentSong!!.imagePath != oldAlbumCoverResource) {
            picasso.load(currentSong!!.imagePath).into(setLockscreenTarget)
            oldAlbumCoverResource = currentSong!!.imagePath
        }
    }

    private inner class SetLockscreen : AsyncTask<Bitmap?, Int?, Int?>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg bitmap: Bitmap?): Int? {
            if (bitmap.isEmpty()) {
                return null
            }
            val curBitmap = bitmap[0] ?: return null
            val widthBitmap = curBitmap.width
            val heightBitmap = curBitmap.height
            //            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//            DisplayMetrics dm = new DisplayMetrics();
//            windowManager.getDefaultDisplay().getMetrics(dm);
            try {
                val visibleCropHint = Rect()
                visibleCropHint[(widthBitmap / 4.6).toInt(), 0, widthBitmap] = heightBitmap
                wallpaperManager.setBitmap(
                    curBitmap,
                    visibleCropHint,
                    false,
                    WallpaperManager.FLAG_LOCK
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //            Timber.d("bm="+String.valueOf(widthBitmap)+","+String.valueOf(heightBitmap)+
//                    ", dm="+String.valueOf(dm.widthPixels)+","+String.valueOf(dm.heightPixels));
            return null
        }
    }

    val setLockscreenTarget: Target = object : Target {
        override fun onBitmapFailed(e: java.lang.Exception, errorDrawable: Drawable) {}
        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
//            new SetLockscreen().execute(bitmap);
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
    }

    fun onCreate() {
        if (currentSong == null) {
//            currentSong = PlayerFragment.currentSong
        }
        createAndShowNotificationPlayer()
    }

    fun onDestroy() {
//        if (state == State.OnDestroy) {
        context.unregisterReceiver(cancelNotifPlayerReceiver)
        context.unregisterReceiver(playNotifPlayerReceiver)
        context.unregisterReceiver(forwardNotifPlayerReceiver)
        context.unregisterReceiver(backwardNotifPlayerReceiver)
//        try {
//            wallpaperManager.clear(WallpaperManager.FLAG_LOCK);
//            wallpaperManager.setBitmap(
//                    oldWallpaper,
//                    null,
//                    false,
//                    WallpaperManager.FLAG_LOCK);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        }
    }

    fun onStartCommand() {
//        sendBroadcast(new Intent(TAG_RESTORE_FPF_PS_TO_F_BR));
    }

    fun createAndShowNotificationPlayer() {
        remoteViewsBigContent = RemoteViews(context.packageName, R.layout.content_notification_player_big)
        remoteViewsContent = RemoteViews(context.packageName, R.layout.content_notification_player)

//        try {
//            int resId = getResources().getIdentifier("brasil", "drawable", getPackageName());

//        if (oldWallpaper == null) {
//            wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
//            FileDescriptor fdOldWallpaper =
//                    wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_LOCK).getFileDescriptor();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            oldWallpaper = BitmapFactory.decodeFileDescriptor(fdOldWallpaper, null, options);
//        }

//            wallpaperManager.setResource(resId, WallpaperManager.FLAG_LOCK);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        addListenersOnRemoteViewsBigAndNonBigContent(remoteViewsBigContent, remoteViewsContent)
        //        addListenersOnRemoteViewsContent(remoteViewsContent);
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("TAG_SHOW_FPF", "showFpf")
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(intent)
        val pendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        var channelId: String = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("PlayerServiceChannelId", "PlayerServiceChannelName")
        }
        mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.play_button)
            .setCustomBigContentView(remoteViewsBigContent)
            .setContent(remoteViewsContent)
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_MAX)
            .setContentIntent(pendingIntent)

//            nm.notify(0, mBuilder.build());
        _state.value = State.Start(mBuilder!!)
        changePlayButtonImage(remoteViewsContent, remoteViewsBigContent)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String
    ): String {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE).apply {
            lightColor = Color.BLUE
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        nm.createNotificationChannel(channel)
        return channelId
    }

    fun changePlayButtonImage(remoteViewsContent: RemoteViews, remoteViewsBigContent: RemoteViews) {
        UpdateLockscreen()
        if (!isPlaying) {
            remoteViewsBigContent.setImageViewResource(
                R.id.playPauseSongButtonNotifPlayerBig,
                R.drawable.pause_lines_button
            )
            remoteViewsContent.setImageViewResource(
                R.id.playPauseSongButtonNotifPlayer,
                R.drawable.pause_lines_button
            )
            isPlaying = true
            smallIcon = R.drawable.play_button
        } else {
            remoteViewsBigContent.setImageViewResource(
                R.id.playPauseSongButtonNotifPlayerBig,
                R.drawable.play_button
            )
            remoteViewsContent.setImageViewResource(
                R.id.playPauseSongButtonNotifPlayer,
                R.drawable.play_button
            )
            isPlaying = false
            smallIcon = R.drawable.pause_lines_button
        }
        if (currentSong != null) {
            val target: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                    remoteViewsBigContent.setImageViewBitmap(
                        R.id.iconAlbumCoverNotifPlayerBig,
                        bitmap
                    )
                    remoteViewsContent.setImageViewBitmap(R.id.iconAlbumCoverNotifPlayer, bitmap)
                }

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
            }
            if (MathUtils.tryParse(currentSong!!.imagePath) != -1) {
                remoteViewsBigContent.setViewPadding(
                    R.id.iconAlbumCoverNotifPlayerBig,
                    80,
                    80,
                    80,
                    80
                )
                remoteViewsContent.setViewPadding(R.id.iconAlbumCoverNotifPlayer, 40, 40, 40, 40)
            } else {
                remoteViewsBigContent.setViewPadding(R.id.iconAlbumCoverNotifPlayerBig, 0, 0, 0, 0)
                remoteViewsContent.setViewPadding(R.id.iconAlbumCoverNotifPlayer, 0, 0, 0, 0)
            }
            picasso.load(currentSong!!.imagePath).into(target)
            //            remoteViewsBigContent.setImageViewResource(R.id.iconAlbumCoverNotifPlayerBig, Integer.parseInt(currentSong!!.getImagePath()));
//            remoteViewsContent.setImageViewResource(R.id.iconAlbumCoverNotifPlayer, Integer.parseInt(currentSong!!.getImagePath()));
            remoteViewsBigContent.setTextViewText(
                R.id.nameSongFullscreenPlayerNotifPlayerBig,
                currentSong!!.title
            )
            remoteViewsContent.setTextViewText(
                R.id.nameSongFullscreenPlayerNotifPlayer,
                currentSong!!.title
            )
            remoteViewsBigContent.setTextViewText(
                R.id.nameArtistFullScreenPlayerNotifPlayerBig,
                currentSong!!.nameArtist
            )
            remoteViewsContent.setTextViewText(
                R.id.nameArtistFullScreenPlayerNotifPlayer,
                currentSong!!.nameArtist
            )
        }
        mBuilder!!.setCustomBigContentView(remoteViewsBigContent)
        mBuilder!!.setContent(remoteViewsContent)
        mBuilder!!.setSmallIcon(smallIcon)

        _state.value = State.OnPlay(mBuilder!!)
    }

    fun addListenersOnRemoteViewsBigAndNonBigContent(
        remoteViewsBigContent: RemoteViews,
        remoteViewsContent: RemoteViews
    ) {
        context.registerReceiver(cancelNotifPlayerReceiver, IntentFilter(TAG_CANCEL_BIG))
        var pi = PendingIntent.getBroadcast(
            context,
            0,
            Intent(TAG_CANCEL_BIG),
            PendingIntent.FLAG_IMMUTABLE
        )
        remoteViewsBigContent.setOnClickPendingIntent(R.id.cancelNotifPlayerBig, pi)
        context.registerReceiver(playNotifPlayerReceiver, IntentFilter(TAG_PLAY))
        pi = PendingIntent.getBroadcast(
            context,
            0,
            Intent(TAG_PLAY),
            PendingIntent.FLAG_IMMUTABLE
        )
        remoteViewsBigContent.setOnClickPendingIntent(R.id.playPauseSongButtonNotifPlayerBig, pi)
        remoteViewsContent.setOnClickPendingIntent(R.id.playPauseSongButtonNotifPlayer, pi)
        forwardNotifPlayerReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Toast.makeText(context, "forward notification big was clicked", Toast.LENGTH_SHORT)
                    .show()
                context.sendBroadcast(Intent(TAG_FORWARD_BUT_PS_TO_F_BR))
            }
        }
        context.registerReceiver(forwardNotifPlayerReceiver, IntentFilter(TAG_FORWARD))
        pi = PendingIntent.getBroadcast(
            context,
            0,
            Intent(TAG_FORWARD),
            PendingIntent.FLAG_IMMUTABLE
        )
        remoteViewsBigContent.setOnClickPendingIntent(R.id.toNextSongButtonNotifPlayerBig, pi)
        remoteViewsContent.setOnClickPendingIntent(R.id.toNextSongButtonNotifPlayer, pi)
        backwardNotifPlayerReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Toast.makeText(context, "backward notification big was clicked", Toast.LENGTH_SHORT)
                    .show()
                context.sendBroadcast(Intent(TAG_BACKWARD_BUT_PS_TO_F_BR))
            }
        }
        context.registerReceiver(backwardNotifPlayerReceiver, IntentFilter(TAG_BACKWARD))
        pi = PendingIntent.getBroadcast(
            context,
            0,
            Intent(TAG_BACKWARD),
            PendingIntent.FLAG_IMMUTABLE
        )
        remoteViewsBigContent.setOnClickPendingIntent(R.id.toPreviousSongButtonNotifPlayerBig, pi)
        remoteViewsContent.setOnClickPendingIntent(R.id.toPreviousSongButtonNotifPlayer, pi)
    }

    fun handlePlayerLogic() {
        try {
//            var fileCurrentSong = PlayerFragment.fileCurrentSong
            val filePlayedSong = PlayerFragment.filePlayedSong
//            val currentSong = PlayerFragment.currentSong
//            val curPosition = PlayerFragment.currentPosition
//            val startTime = PlayerFragment.startTime
//            val finalTime = PlayerFragment.finalTime
//            val resume = PlayerFragment.isResumed
//            if (fileCurrentSong == null) {
//                fileCurrentSong = File(currentSong!!.path)
//                musicPlayer.mediaPlayer.setDataSource(fileCurrentSong!!.toString())
//            }
//            if (filePlayedSong !== fileCurrentSong) {
//                val list = listRecentlySongs
//                if (list.size == 0 || list[0] !== currentSong) {
//                    list.addFirst(currentSong)
//                    PlayerFragment.filePlayedSong = fileCurrentSong
//                }
//            }
            if (isPlaying) {
//                if (!resume) {
//                    musicPlayer.mediaPlayer.prepare()
//                    musicPlayer.mediaPlayer.start()
//                    PlayerFragment.startTime = musicPlayer.mediaPlayer.currentPosition?.toDouble() ?: 0.0
//                    PlayerFragment.finalTime = musicPlayer.mediaPlayer.duration?.toDouble() ?: 0.0
                    //                    seekBar.setMax((int) finalTime);
//                    seekBar.setProgress((int)startTime);
//                    myHandler.postDelayed(UpdateSongTime,10);
//                    myHandler.postDelayed(UpdateSeekBar,10);
//                    PlayerFragment.isResumed = true
//                    PlayerFragment.isPaused = false
//                } else {
//                    musicPlayer.mediaPlayer.seekTo(curPosition)
//                    musicPlayer.mediaPlayer.start()
//                    PlayerFragment.isPaused = false
//                }
            } else {
                musicPlayer.pause()
//                PlayerFragment.currentPosition = musicPlayer.mediaPlayer.currentPosition ?: 0
//                PlayerFragment.isPaused = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    sealed class State {
        object OnDestroy : State()
        object OnCancel : State()
        class OnPlay(val builder: NotificationCompat.Builder) : State()
        class Start(val builder: NotificationCompat.Builder) : State()
    }
}