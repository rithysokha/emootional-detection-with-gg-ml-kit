package com.emotion.detector.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {
    @Insert
    fun insert(notification: Notification)

    @Query("SELECT * FROM notifications")
    fun getAllNotifications(): LiveData<List<Notification>>

    @Query("DELETE FROM notifications")
    fun clearNotifications()
}