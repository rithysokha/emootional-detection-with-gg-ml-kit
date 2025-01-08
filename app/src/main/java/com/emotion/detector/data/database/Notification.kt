package com.emotion.detector.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

