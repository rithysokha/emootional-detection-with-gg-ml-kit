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

        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            sendNotification(it.body)
        }

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

    private fun sendNotification(messageBody: String?) {
        val channelId = "default_channel_id"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("App Update")
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }
}
