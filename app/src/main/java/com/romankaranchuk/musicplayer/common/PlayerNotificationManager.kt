package com.romankaranchuk.musicplayer.common

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

interface IPlayerNotificationManager

class PlayerNotificationManager(
    private val nm: NotificationManager,
    private val context: Context
) : IPlayerNotificationManager