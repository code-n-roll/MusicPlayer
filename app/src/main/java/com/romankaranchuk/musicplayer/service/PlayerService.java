package com.romankaranchuk.musicplayer.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.LinkedList;

import com.romankaranchuk.musicplayer.ui.player.PlayerFragment;
import com.romankaranchuk.musicplayer.ui.main.MainActivity;
import com.romankaranchuk.musicplayer.R;
import com.romankaranchuk.musicplayer.data.Song;
import com.romankaranchuk.musicplayer.utils.MathUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PlayerService extends Service {
    NotificationManager nm;
    WallpaperManager wallpaperManager;
    Bitmap oldWallpaper;
    BroadcastReceiver cancelNotifPlayerReceiver, playNotifPlayerReceiver,
            forwardNotifPlayerReceiver, backwardNotifPlayerReceiver;

    String  TAG_CANCEL_BIG = "cancelNotifPlayerReceiver",
            TAG_FORWARD = "forwardNotifPlayerReceiver",
            TAG_PLAY = "playNotifPlayerReceiver",
            TAG_BACKWARD = "backwardNotifPlayerReceiver",
            LOG_TAG = "myLogs",
            TAG_PLAY_BUT_PS_TO_F_BR = "playButtonFromPStoFragmentBR",
            TAG_FORWARD_BUT_PS_TO_F_BR = "forwardButtonFromPStoFragmentBR",
            TAG_BACKWARD_BUT_PS_TO_F_BR = "backwardButtonFromPStoFragmentBR",
            TAG_RESTORE_FPF_PS_TO_F_BR="restoreFpfFromPStoF";
    NotificationCompat.Builder mBuilder;
    boolean isPlaying = false;
    PlayerBinder binder = new PlayerBinder();
    private MediaPlayer mediaPlayer;
    private File path;
    int smallIcon;
    private Song currentSong;
    private String oldAlbumCoverResource;
    public RemoteViews remoteViewsBigContent = null,remoteViewsContent= null;

    public void UpdateLockscreen(){
        if (currentSong != null && !currentSong.getImagePath().equals(oldAlbumCoverResource)) {
            Picasso.with(getApplicationContext()).load(currentSong.getImagePath()).into(setLockscreenTarget);
            oldAlbumCoverResource = currentSong.getImagePath();
        }
    }

    private class SetLockscreen extends AsyncTask<Bitmap, Integer, Integer>{
        @Override
        protected Integer doInBackground(Bitmap... bitmap) {
            Bitmap curBitmap = bitmap[0];
            int widthBitmap = curBitmap.getWidth(),
                    heightBitmap = curBitmap.getHeight();
//            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//            DisplayMetrics dm = new DisplayMetrics();
//            windowManager.getDefaultDisplay().getMetrics(dm);
            try {
                Rect visibleCropHint = new Rect();
                visibleCropHint.set(
                        (int)(widthBitmap/4.6),
                    0,
                    widthBitmap,
                    heightBitmap
                );
                wallpaperManager.setBitmap(curBitmap, visibleCropHint, false, WallpaperManager.FLAG_LOCK);
            } catch (IOException e){
                e.printStackTrace();
            }
//            Log.d("MyLogs", "bm="+String.valueOf(widthBitmap)+","+String.valueOf(heightBitmap)+
//                    ", dm="+String.valueOf(dm.widthPixels)+","+String.valueOf(dm.heightPixels));
            return null;
        }
    }

    final Target setLockscreenTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            new SetLockscreen().execute(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };



    public MediaPlayer getMediaPlayer(){
        return this.mediaPlayer;
    }


    @Override
    public void onCreate(){
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (currentSong == null){
            currentSong = PlayerFragment.getCurrentSong();
        }
        sendNotification();
        if (PlayerFragment.getCurMediaPlayer() == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        Log.d(LOG_TAG,"PlayerService onCreate");
    }



    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(LOG_TAG, "PlayerService onStartCommand");
        if (mediaPlayer == null){
            mediaPlayer = PlayerFragment.getCurMediaPlayer();
        }

//        sendBroadcast(new Intent(TAG_RESTORE_FPF_PS_TO_F_BR));
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(cancelNotifPlayerReceiver);
        unregisterReceiver(playNotifPlayerReceiver);
        unregisterReceiver(forwardNotifPlayerReceiver);
        unregisterReceiver(backwardNotifPlayerReceiver);
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
        Log.d(LOG_TAG, "PlayerService onDestroy");
    }



    void sendNotification() {
        remoteViewsBigContent = new RemoteViews(getPackageName(), R.layout.content_notification_player_big);
        remoteViewsContent = new RemoteViews(getPackageName(), R.layout.content_notification_player);

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


        addListenersOnRemoteViewsBigAndNonBigContent(remoteViewsBigContent, remoteViewsContent);
//        addListenersOnRemoteViewsContent(remoteViewsContent);


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("TAG_SHOW_FPF", "showFpf");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
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

    public class PlayerBinder extends Binder {
        public PlayerService getService(){
            return PlayerService.this;
        }
    }



    void changePlayButtonImage(final RemoteViews remoteViewsContent, final RemoteViews remoteViewsBigContent){
        UpdateLockscreen();
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

            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    remoteViewsBigContent.setImageViewBitmap(R.id.iconAlbumCoverNotifPlayerBig, bitmap);
                    remoteViewsContent.setImageViewBitmap(R.id.iconAlbumCoverNotifPlayer, bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };


            if (MathUtils.tryParse(currentSong.getImagePath()) != -1){
                remoteViewsBigContent.setViewPadding(R.id.iconAlbumCoverNotifPlayerBig,80,80,80,80);
                remoteViewsContent.setViewPadding(R.id.iconAlbumCoverNotifPlayer,40,40,40,40);
            } else {
                remoteViewsBigContent.setViewPadding(R.id.iconAlbumCoverNotifPlayerBig,0,0,0,0);
                remoteViewsContent.setViewPadding(R.id.iconAlbumCoverNotifPlayer,0,0,0,0);
            }
            Picasso.with(this).load(currentSong.getImagePath()).into(target);
//            remoteViewsBigContent.setImageViewResource(R.id.iconAlbumCoverNotifPlayerBig, Integer.parseInt(currentSong.getImagePath()));
//            remoteViewsContent.setImageViewResource(R.id.iconAlbumCoverNotifPlayer, Integer.parseInt(currentSong.getImagePath()));
            remoteViewsBigContent.setTextViewText(R.id.nameSongFullscreenPlayerNotifPlayerBig, currentSong.getTitle());
            remoteViewsContent.setTextViewText(R.id.nameSongFullscreenPlayerNotifPlayer, currentSong.getTitle());
            remoteViewsBigContent.setTextViewText(R.id.nameArtistFullScreenPlayerNotifPlayerBig, currentSong.getNameArtist());
            remoteViewsContent.setTextViewText(R.id.nameArtistFullScreenPlayerNotifPlayer, currentSong.getNameArtist());
        }
        mBuilder.setCustomBigContentView(remoteViewsBigContent);
        mBuilder.setContent(remoteViewsContent);
        mBuilder.setSmallIcon(smallIcon);
        startForeground(1, mBuilder.build());
    }
    public void addListenersOnRemoteViewsBigAndNonBigContent(final RemoteViews remoteViewsBigContent, final RemoteViews remoteViewsContent){
        cancelNotifPlayerReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                Toast.makeText(context,"notification big was deleted", Toast.LENGTH_SHORT).show();
                if (isPlaying)
                    playNotifPlayerReceiver.onReceive(context,intent);
                nm.cancel(1);
                stopSelf();
            }
        };
        registerReceiver(cancelNotifPlayerReceiver, new IntentFilter(TAG_CANCEL_BIG));
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_CANCEL_BIG),0);
        remoteViewsBigContent.setOnClickPendingIntent(R.id.cancelNotifPlayerBig, pi);


        playNotifPlayerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "play notification big was clicked", Toast.LENGTH_SHORT).show();
                if (intent.getParcelableExtra("currentSong")!=null){
                    currentSong = intent.getParcelableExtra("currentSong");
                    isPlaying = intent.getBooleanExtra("isPlaying", false);
                }


                changePlayButtonImage(remoteViewsContent, remoteViewsBigContent);

                if (intent.getStringExtra("fpfCall") == null) {
                    handlePlayerLogic();
                    sendBroadcast(new Intent(TAG_PLAY_BUT_PS_TO_F_BR));
                }
            }
        };
        registerReceiver(playNotifPlayerReceiver, new IntentFilter(TAG_PLAY));
        pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_PLAY),0);
        remoteViewsBigContent.setOnClickPendingIntent(R.id.playPauseSongButtonNotifPlayerBig, pi);
        remoteViewsContent.setOnClickPendingIntent(R.id.playPauseSongButtonNotifPlayer, pi);

        forwardNotifPlayerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "forward notification big was clicked", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(TAG_FORWARD_BUT_PS_TO_F_BR));
            }
        };
        registerReceiver(forwardNotifPlayerReceiver, new IntentFilter(TAG_FORWARD));
        pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_FORWARD),0);
        remoteViewsBigContent.setOnClickPendingIntent(R.id.toNextSongButtonNotifPlayerBig, pi);
        remoteViewsContent.setOnClickPendingIntent(R.id.toNextSongButtonNotifPlayer, pi);

        backwardNotifPlayerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "backward notification big was clicked", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(TAG_BACKWARD_BUT_PS_TO_F_BR));
            }
        };
        registerReceiver(backwardNotifPlayerReceiver, new IntentFilter(TAG_BACKWARD));
        pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_BACKWARD),0);
        remoteViewsBigContent.setOnClickPendingIntent(R.id.toPreviousSongButtonNotifPlayerBig, pi);
        remoteViewsContent.setOnClickPendingIntent(R.id.toPreviousSongButtonNotifPlayer, pi);

    }

    public void handlePlayerLogic(){
        try {
            File fileCurrentSong = PlayerFragment.getFileCurrentSong();
            File filePlayedSong = PlayerFragment.getFilePlayedSong();
            Song currentSong = PlayerFragment.getCurrentSong();
            int curPosition = PlayerFragment.getCurPosition();
            double startTime = PlayerFragment.getStartTime(),
                    finalTime = PlayerFragment.getFinalTime();
            boolean resume = PlayerFragment.getResume();
            if (fileCurrentSong == null) {
                fileCurrentSong = new File (currentSong.getPath());
                mediaPlayer.setDataSource(fileCurrentSong.toString());
            }
            if (filePlayedSong != fileCurrentSong) {
                LinkedList<Song> list = MainActivity.getListRecentlySongs();
                if (list.size() == 0 || list.get(0) != currentSong) {
                    list.addFirst(currentSong);
                    PlayerFragment.setFilePlayedSong(fileCurrentSong);
                }
            }
            if (isPlaying) {
                if (!resume) {
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    PlayerFragment.setStartTime(mediaPlayer.getCurrentPosition());
                    PlayerFragment.setFinalTime(mediaPlayer.getDuration());
//                    seekBar.setMax((int) finalTime);
//                    seekBar.setProgress((int)startTime);
//                    myHandler.postDelayed(UpdateSongTime,10);
//                    myHandler.postDelayed(UpdateSeekBar,10);
                    PlayerFragment.setResume(true);
                    PlayerFragment.setPaused(false);
                } else {
                    mediaPlayer.seekTo(curPosition);
                    mediaPlayer.start();
                    PlayerFragment.setPaused(false);
                }
            } else {
                mediaPlayer.pause();
                PlayerFragment.setCurPosition(mediaPlayer.getCurrentPosition());
                PlayerFragment.setPaused(true);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
