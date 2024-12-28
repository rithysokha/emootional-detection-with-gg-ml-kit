package com.emotion.detector.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emotion.detector.R
import com.emotion.detector.model.Notification

class NotificationAdapter(private val notificationList: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    init {
        Log.d("NotificationAdapter", "Adapter initialized with ${notificationList.size} items")
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvItemTitle)
        val description: TextView = itemView.findViewById(R.id.tvItemDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        Log.d("NotificationAdapter", "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        Log.d("NotificationAdapter", "onBindViewHolder called for position $position")
        val notification = notificationList[position]
        holder.title.text = notification.title
        holder.description.text = notification.description
    }

    override fun getItemCount(): Int {
        Log.d("NotificationAdapter", "getItemCount called, returning ${notificationList.size}")
        return notificationList.size
    }
}