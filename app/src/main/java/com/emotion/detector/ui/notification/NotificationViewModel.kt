package com.emotion.detector.ui.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.emotion.detector.data.database.Notification
import com.emotion.detector.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationDao = AppDatabase.getDatabase(application).notificationDao()
    val notifications: LiveData<List<Notification>> = notificationDao.getAllNotifications()

    fun insert(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.insert(notification)
        }
    }
}
