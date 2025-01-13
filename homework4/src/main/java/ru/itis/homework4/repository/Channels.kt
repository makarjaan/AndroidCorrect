package ru.itis.homework4.repository

import android.app.NotificationManager
import ru.itis.homework4.model.NotificationChannelData

object Channels {
    val notificationChannelData = listOf(
        NotificationChannelData(
            id = "default_channel_id",
            name = "Стандартный канал уведомления",
            importance = NotificationManager.IMPORTANCE_DEFAULT
        ),
        NotificationChannelData(
            id = "max_channel_id",
            name = "Канал уведомления с максимальным приоритетом",
            importance = NotificationManager.IMPORTANCE_MAX
        ),
        NotificationChannelData(
            id = "high_channel_id",
            name = "Канал уведомления с высоким приоритетом",
            importance = NotificationManager.IMPORTANCE_HIGH
        ),
        NotificationChannelData(
            id = "low_channel_id",
            name = "Канал уведомления с низким приоритетом",
            importance = NotificationManager.IMPORTANCE_LOW
        )
    )
}