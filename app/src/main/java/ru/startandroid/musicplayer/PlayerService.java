package ru.startandroid.musicplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PlayerService extends Service {
    NotificationManager nm;
    WallpaperManager wallpaperManager;
    Bitmap oldWallpaper;
    BroadcastReceiver cancelNotifPlayerBigReceiver,  playNotifPlayerBigReceiver,
                          forwardNotifPlayerBigReceiver, backwardNotifPlayerBigReceiver,
                          playNotifPlayerReceiver,  forwardNotifPlayerReceiver,
                          backwardNotifPlayerReceiver;

    String TAG_PLAY_BIG = "playNotifPlayerBigReceiver",
           TAG_FORWARD_BIG = "forwardNotifPlayerBigReceiver",TAG_BACKWARD_BIG = "backwardNotifPlayerBigReceiver",
            TAG_CANCEL_BIG = "cancelNotifPlayerBigReceiver", TAG_FORWARD = "forwardNotifPlayerReceiver",
            TAG_PLAY = "playNotifPlayerReceiver",TAG_BACKWARD = "backwardNotifPlayerReceiver",
            LOG_TAG = "myLogs", TAG_PLAY_BUT_PS_TO_F_BR = "playButtonFromPStoFragmentBR",
            TAG_FORWARD_BUT_PS_TO_F_BR = "forwardButtonFromPStoFragmentBR",
            TAG_BACKWARD_BUT_PS_TO_F_BR = "backwardButtonFromPStoFragmentBR";
    NotificationCompat.Builder mBuilder;
    boolean isPlaying = true;
    PlayerBinder binder = new PlayerBinder();
    private MediaPlayer mediaPlayer;
    private File path;
    int smallIcon;
    private SongCardView currentSong;
    private int oldAlbumCoverResource;
    Thread UpdateLockscreen = new Thread(new Runnable() {
        @TargetApi(24)
        @Override
        public void run() {
            try {
                if (currentSong != null && currentSong.getAlbumCoverResource() != oldAlbumCoverResource) {
                    wallpaperManager.setResource(currentSong.getAlbumCoverResource(), WallpaperManager.FLAG_LOCK);
                    oldAlbumCoverResource = currentSong.getAlbumCoverResource();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    });



    public MediaPlayer getMediaPlayer(){
        return this.mediaPlayer;
    }

    public File getPath(){
        return this.path;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sendNotification();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC);
//        FullscreenPlayerFragment.setPath(path);
//        FullscreenPlayerFragment.setCurMediaPlayer(mediaPlayer);
        Log.d(LOG_TAG,"PlayerService onCreate");
    }



    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(LOG_TAG, "PlayerService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(24)
    public void onDestroy(){
        super.onDestroy();
//        this.unregisterReceiver(playNotifPlayerReceiver);
//        this.unregisterReceiver(forwardNotifPlayerReceiver);
//        this.unregisterReceiver(backwardNotifPlayerReceiver);
        this.unregisterReceiver(cancelNotifPlayerBigReceiver);
        this.unregisterReceiver(playNotifPlayerBigReceiver);
        this.unregisterReceiver(forwardNotifPlayerBigReceiver);
        this.unregisterReceiver(backwardNotifPlayerBigReceiver);
        try {
            wallpaperManager.clear(WallpaperManager.FLAG_LOCK);
            wallpaperManager.setBitmap(
                    oldWallpaper,
                    null,
                    false,
                    WallpaperManager.FLAG_LOCK);
        } catch (IOException e){
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "PlayerService onDestroy");
    }



    @TargetApi(24)
    void sendNotification() {
        RemoteViews remoteViewsBigContent = new RemoteViews(getPackageName(), R.layout.content_notification_player_big);
        RemoteViews remoteViewsContent = new RemoteViews(getPackageName(), R.layout.content_notification_player);

        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
//        try {
//            int resId = getResources().getIdentifier("brasil", "drawable", getPackageName());
            FileDescriptor fdOldWallpaper =
                    wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_LOCK).getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            oldWallpaper = BitmapFactory.decodeFileDescriptor(fdOldWallpaper, null, options);
//            wallpaperManager.setResource(resId, WallpaperManager.FLAG_LOCK);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        addListenersOnRemoteViewsBigAndNonBigContent(remoteViewsBigContent, remoteViewsContent);
//        addListenersOnRemoteViewsContent(remoteViewsContent);


        Intent intent = new Intent(this, TracklistActivity.class);
        intent.putExtra("TAG_SHOW_FPF", "showFpf");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TracklistActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.play_button)
                .setCustomBigContentView(remoteViewsBigContent)
                .setContent(remoteViewsContent)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

//            nm.notify(0, mBuilder.build());
        startForeground(1, mBuilder.build());

        changePlayButtonImage(remoteViewsContent, remoteViewsBigContent);
    }

    public IBinder onBind(Intent intent){
        Log.d(LOG_TAG, "PlayerService onBind");
        return binder;
    }

    class PlayerBinder extends Binder {
        PlayerService getService(){
            return PlayerService.this;
        }
    }


//    public void addListenersOnRemoteViewsContent(RemoteViews remoteViewsContent){
//        PendingIntent pi;
//
//        playNotifPlayerReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(context, "play notification was clicked", Toast.LENGTH_LONG).show();
//            }
//        };
//        this.registerReceiver(playNotifPlayerReceiver, new IntentFilter(TAG_PLAY));
//        pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_PLAY),0);
//        remoteViewsContent.setOnClickPendingIntent(R.id.playPauseSongButtonNotifPlayer, pi);
//
//        forwardNotifPlayerReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(context, "forward notification was clicked", Toast.LENGTH_LONG).show();
//            }
//        };
//        this.registerReceiver(forwardNotifPlayerReceiver, new IntentFilter(TAG_FORWARD));
//        pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_FORWARD),0);
//        remoteViewsContent.setOnClickPendingIntent(R.id.toNextSongButtonNotifPlayer, pi);
//
//        backwardNotifPlayerReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(context, "backward notification was clicked", Toast.LENGTH_LONG).show();
//            }
//        };
//        this.registerReceiver(backwardNotifPlayerReceiver, new IntentFilter(TAG_BACKWARD));
//        pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_BACKWARD),0);
//        remoteViewsContent.setOnClickPendingIntent(R.id.toPreviousSongButtonNotifPlayer, pi);
//    }
    @TargetApi(24)
    public void changePlayButtonImage(RemoteViews remoteViewsContent, RemoteViews remoteViewsBigContent){
        UpdateLockscreen.start();
        if (!isPlaying) {
            remoteViewsBigContent.setImageViewResource(R.id.playPauseSongButtonNotifPlayerBig, R.drawable.pause_lines_button);
            remoteViewsContent.setImageViewResource(R.id.playPauseSongButtonNotifPlayer, R.drawable.pause_lines_button);
            isPlaying = true;
            smallIcon = R.drawable.play_button;
        } else {
            remoteViewsBigContent.setImageViewResource(R.id.playPauseSongButtonNotifPlayerBig, R.drawable.play_button);
            remoteViewsContent.setImageViewResource(R.id.playPauseSongButtonNotifPlayer, R.drawable.play_button);
            isPlaying = false;
            smallIcon = R.drawable.pause_lines_button;
        }
        if (currentSong != null) {
            remoteViewsBigContent.setImageViewResource(R.id.iconAlbumCoverNotifPlayerBig, currentSong.getAlbumCoverResource());
            remoteViewsContent.setImageViewResource(R.id.iconAlbumCoverNotifPlayer, currentSong.getAlbumCoverResource());
            remoteViewsBigContent.setTextViewText(R.id.nameSongFullscreenPlayerNotifPlayerBig, currentSong.getNameSong());
            remoteViewsContent.setTextViewText(R.id.nameSongFullscreenPlayerNotifPlayer, currentSong.getNameSong());
            remoteViewsBigContent.setTextViewText(R.id.nameArtistFullScreenPlayerNotifPlayerBig, currentSong.getNameArtist());
            remoteViewsContent.setTextViewText(R.id.nameArtistFullScreenPlayerNotifPlayer, currentSong.getNameArtist());
        }
        mBuilder.setCustomBigContentView(remoteViewsBigContent);
        mBuilder.setContent(remoteViewsContent);
        mBuilder.setSmallIcon(smallIcon);
        startForeground(1, mBuilder.build());
    }
    public void addListenersOnRemoteViewsBigAndNonBigContent(final RemoteViews remoteViewsBigContent, final RemoteViews remoteViewsContent){
        cancelNotifPlayerBigReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                Toast.makeText(context,"notification big was deleted", Toast.LENGTH_SHORT).show();
                nm.cancel(1);
                stopSelf();
            }
        };
        this.registerReceiver(cancelNotifPlayerBigReceiver, new IntentFilter(TAG_CANCEL_BIG));
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_CANCEL_BIG),0);
        remoteViewsBigContent.setOnClickPendingIntent(R.id.cancelNotifPlayerBig, pi);


        playNotifPlayerBigReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "play notification big was clicked", Toast.LENGTH_SHORT).show();
//                remoteViewsBigContent.setImageViewResource(R.id.playPauseSongButtonNotifPlayerBig, R.drawable.play_button);
                if (intent.getParcelableExtra("currentSong")!=null){
                    currentSong = intent.getParcelableExtra("currentSong");
                    isPlaying = intent.getBooleanExtra("isPlaying", false);
                }
                changePlayButtonImage(remoteViewsContent, remoteViewsBigContent);

                if (intent.getStringExtra("fpfCall") == null) {
                    handlePlayerLogic();
                    getApplication().sendBroadcast(new Intent(TAG_PLAY_BUT_PS_TO_F_BR));
                }
            }
        };
        this.registerReceiver(playNotifPlayerBigReceiver, new IntentFilter(TAG_PLAY));
        pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_PLAY),0);
        remoteViewsBigContent.setOnClickPendingIntent(R.id.playPauseSongButtonNotifPlayerBig, pi);
        remoteViewsContent.setOnClickPendingIntent(R.id.playPauseSongButtonNotifPlayer, pi);

        forwardNotifPlayerBigReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "forward notification big was clicked", Toast.LENGTH_SHORT).show();
                getApplication().sendBroadcast(new Intent(TAG_FORWARD_BUT_PS_TO_F_BR));
            }
        };
        this.registerReceiver(forwardNotifPlayerBigReceiver, new IntentFilter(TAG_FORWARD));
        pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_FORWARD),0);
        remoteViewsBigContent.setOnClickPendingIntent(R.id.toNextSongButtonNotifPlayerBig, pi);
        remoteViewsContent.setOnClickPendingIntent(R.id.toNextSongButtonNotifPlayer, pi);

        backwardNotifPlayerBigReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "backward notification big was clicked", Toast.LENGTH_SHORT).show();
                getApplication().sendBroadcast(new Intent(TAG_BACKWARD_BUT_PS_TO_F_BR));
            }
        };
        this.registerReceiver(backwardNotifPlayerBigReceiver, new IntentFilter(TAG_BACKWARD));
        pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_BACKWARD),0);
        remoteViewsBigContent.setOnClickPendingIntent(R.id.toPreviousSongButtonNotifPlayerBig, pi);
        remoteViewsContent.setOnClickPendingIntent(R.id.toPreviousSongButtonNotifPlayer, pi);

    }

    public void handlePlayerLogic(){
        try {
            File fileCurrentSong = FullscreenPlayerFragment.getFileCurrentSong();
            File filePlayedSong = FullscreenPlayerFragment.getFilePlayedSong();
            SongCardView currentSong = FullscreenPlayerFragment.getCurrentSong();
            int curPosition = FullscreenPlayerFragment.getCurPosition();
            double startTime = FullscreenPlayerFragment.getStartTime(),
                    finalTime = FullscreenPlayerFragment.getFinalTime();
            boolean resume = FullscreenPlayerFragment.getResume(),
            paused = FullscreenPlayerFragment.getPaused();
            if (fileCurrentSong == null) {
                fileCurrentSong = new File (path,currentSong.getFilePath());
                mediaPlayer.setDataSource(fileCurrentSong.toString());
            }
            if (filePlayedSong != fileCurrentSong) {
                ArrayList<SongCardView> list = MainActivity.getListRecentlySongs();
                if (list.size() == 0 || list.get(0) != currentSong) {
                    Collections.reverse(list);
                    list.add(list.size(), currentSong);
                    Collections.reverse(list);
                    FullscreenPlayerFragment.setFilePlayedSong(fileCurrentSong);
                }
            }
            if (isPlaying) {
                if (!resume) {
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    FullscreenPlayerFragment.setStartTime(mediaPlayer.getCurrentPosition());
                    FullscreenPlayerFragment.setFinalTime(mediaPlayer.getDuration());
//                    seekBar.setMax((int) finalTime);
//                    seekBar.setProgress((int)startTime);
//                    myHandler.postDelayed(UpdateSongTime,10);
//                    myHandler.postDelayed(UpdateSeekBar,10);
                    FullscreenPlayerFragment.setResume(true);
                    FullscreenPlayerFragment.setPaused(false);
                } else {
                    mediaPlayer.seekTo(curPosition);
                    mediaPlayer.start();
                    FullscreenPlayerFragment.setPaused(false);
                }
            } else {
                mediaPlayer.pause();
                FullscreenPlayerFragment.setCurPosition(mediaPlayer.getCurrentPosition());
                FullscreenPlayerFragment.setPaused(true);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
