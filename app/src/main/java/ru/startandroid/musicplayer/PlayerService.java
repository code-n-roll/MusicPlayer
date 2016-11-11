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
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayerService extends Service {
        NotificationManager nm;
        WallpaperManager wallpaperManager;
        Bitmap oldWallpaper;
        BroadcastReceiver cancelNotifPlayerReceiver,
                              notifPlayerReceiver,
                              playNotifPlayerReceiver,
                              forwardNotifPlayerReceiver,
                              backwardNotifPlayerReceiver;

        String TAG_CANCEL = "cancelNotifPlayerReceiver",
               TAG_PLAYER = "notifPlayerReceiver",
               TAG_PLAY = "playNotifPlayerReceiver",
               TAG_FORWARD = "forwardNotifPlayerReceiver",
               TAG_BACKWARD = "backwardNotifPlayerReceiver",
                TAG_LOG = "myLogs";
        NotificationCompat.Builder mBuilder;

        @Override
        public void onCreate(){
            super.onCreate();
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            sendNotif();
            Log.d(TAG_LOG,"PlayerService onCreate");
        }



        public int onStartCommand(Intent intent, int flags, int startId){
            Log.d(TAG_LOG, "PlayerService onStartCommand");
            return super.onStartCommand(intent, flags, startId);
        }

        @TargetApi(24)
        public void onDestroy(){
            super.onDestroy();
            this.unregisterReceiver(cancelNotifPlayerReceiver);
            this.unregisterReceiver(playNotifPlayerReceiver);
            this.unregisterReceiver(forwardNotifPlayerReceiver);
            this.unregisterReceiver(backwardNotifPlayerReceiver);
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
            Log.d(TAG_LOG, "PlayerService onDestroy");
        }

        @TargetApi(24)
        void sendNotif() {
            RemoteViews remoteViewsBigContent = new RemoteViews(getPackageName(), R.layout.content_notification_player_big);
            RemoteViews remoteViewsContent = new RemoteViews(getPackageName(), R.layout.content_notification_player);

            wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                int resId = getResources().getIdentifier("brasil", "drawable", getPackageName());
                FileDescriptor fdOldWallpaper =
                        wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_LOCK).getFileDescriptor();
                BitmapFactory.Options options = new BitmapFactory.Options();
                oldWallpaper = BitmapFactory.decodeFileDescriptor(fdOldWallpaper, null, options);
                wallpaperManager.setResource(resId, WallpaperManager.FLAG_LOCK);
            } catch (IOException e) {
                e.printStackTrace();
            }

            cancelNotifPlayerReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent){
                    Toast.makeText(context,"notification was deleted", Toast.LENGTH_SHORT).show();
                    nm.cancel(1);
                    stopSelf();
                }
            };
            this.registerReceiver(cancelNotifPlayerReceiver, new IntentFilter(TAG_CANCEL));
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_CANCEL),0);
            remoteViewsBigContent.setOnClickPendingIntent(R.id.cancelNotifPlayer, pi);

            playNotifPlayerReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(context, "play notification was clicked", Toast.LENGTH_SHORT).show();
                }
            };
            this.registerReceiver(playNotifPlayerReceiver, new IntentFilter(TAG_PLAY));
            pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_PLAY),0);
            remoteViewsBigContent.setOnClickPendingIntent(R.id.playPauseSongButtonNotifPlayer, pi);

            forwardNotifPlayerReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(context, "forward notification was clicked", Toast.LENGTH_SHORT).show();
                }
            };
            this.registerReceiver(forwardNotifPlayerReceiver, new IntentFilter(TAG_FORWARD));
            pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_FORWARD),0);
            remoteViewsBigContent.setOnClickPendingIntent(R.id.toNextSongButtonNotifPlayer, pi);

            backwardNotifPlayerReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(context, "backward notification was clicked", Toast.LENGTH_SHORT).show();
                }
            };
            this.registerReceiver(backwardNotifPlayerReceiver, new IntentFilter(TAG_BACKWARD));
            pi = PendingIntent.getBroadcast(this, 0, new Intent(TAG_BACKWARD),0);
            remoteViewsBigContent.setOnClickPendingIntent(R.id.toPreviousSongButtonNotifPlayer, pi);


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
        }

        public IBinder onBind(Intent intent){
            return null;
        }
    }
