package ru.itis.homework4.handler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ru.itis.homework4.R
import ru.itis.homework4.activity.MainActivity
import ru.itis.homework4.model.NotificationData
import ru.itis.homework4.model.NotificationType
import ru.itis.homework4.repository.Channels

class NotificationsHandler(private val appCtx: Context) {

    private val notificationsManager =
        appCtx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationsChannelsIfNeeded()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationsChannelsIfNeeded() {
        Channels.notificationChannelData.forEach { channelData ->
            if (notificationsManager.getNotificationChannel(channelData.id) == null) {
                val channel = with(channelData) {
                    NotificationChannel(id, name, importance)
                }
                notificationsManager.createNotificationChannel(channel)
            }
        }
    }

    fun showNotification(data: NotificationData) {
        val index = when (data.notificationType) {
            NotificationType.MAX -> {
                1
            }
            NotificationType.HIGH -> {
                2
            }
            NotificationType.LOW -> {
                3
            }
            else -> 0
        }

        val channelId = Channels.notificationChannelData[index].id

        val intent = Intent(appCtx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(START_NOTIFICATION_TAG, true)
        }
        val pendingIntent = PendingIntent.getActivity(
            appCtx,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(appCtx, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(data.title)
            .setContentText(data.text)
            .setChannelId(channelId)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationsManager.notify(data.id, notificationBuilder.build())
    }


    companion object {
        const val START_NOTIFICATION_TAG = "start_with_notification"
    }

}