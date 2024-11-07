package com.emotion.detector.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emotion.detector.R

class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    val emotionTextView: TextView = itemView.findViewById(R.id.emotionText)
    val usernameTextView: TextView = itemView.findViewById(R.id.usernameText)

}