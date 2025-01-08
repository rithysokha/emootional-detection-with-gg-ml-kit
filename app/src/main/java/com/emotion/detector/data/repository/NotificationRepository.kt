package com.emotion.detector.data.repository

import androidx.lifecycle.LiveData
import com.emotion.detector.data.database.Notification
import com.emotion.detector.data.database.NotificationDao

class NotificationRepository(private val notificationDao: NotificationDao) {

    // Insert notification into the database
    suspend fun insertNotification(notification: Notification) {
        notificationDao.insert(notification)
    }

    // Get all notifications (as LiveData)
    fun getAllNotifications(): LiveData<List<Notification>> {
        return notificationDao.getAllNotifications()
    }

    // Clear all notifications from the database
    suspend fun clearNotifications() {
        notificationDao.clearNotifications()
    }
}
