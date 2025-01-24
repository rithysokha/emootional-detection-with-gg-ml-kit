package com.emotion.detector

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.emotion.detector.data.database.AppDatabase
import com.emotion.detector.data.database.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PushNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title ?: "No Title"
        val description = remoteMessage.notification?.body ?: "No Description"
        val imageUrl = remoteMessage.notification?.imageUrl?.toString()

        saveNotification(Notification(0, title, description, imageUrl))
    }

    private fun saveNotification(notification: Notification) {
        val notificationDao = AppDatabase.getDatabase(this).notificationDao()
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("FCM", "Saving notification: $notification")
            notificationDao.insert(notification)
            Log.d("FCM", "Notification saved successfully")
        }
    }

}
