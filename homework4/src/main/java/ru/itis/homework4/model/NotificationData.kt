package ru.itis.homework4.model

data class NotificationData(
    val id: Int,
    val title: String,
    val text: String,
    val notificationType: NotificationType? = null
)
