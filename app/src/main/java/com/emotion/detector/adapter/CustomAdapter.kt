package com.emotion.detector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emotion.detector.R
import com.emotion.detector.model.History

class CustomAdapter(private val mList: ArrayList<History>):
RecyclerView.Adapter<CustomViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_design,parent,false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentHistory = mList[position]
        holder.dateTextView.text = currentHistory.created_at
        holder.emotionTextView.text = currentHistory.emotion
        holder.usernameTextView.text = currentHistory.name
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}