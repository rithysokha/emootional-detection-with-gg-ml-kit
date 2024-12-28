package com.emotion.detector.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emotion.detector.R
import com.emotion.detector.adapter.NotificationAdapter
import com.emotion.detector.model.Notification

class NotificationListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("NotificationListFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("NotificationListFragment", "onViewCreated called")

        try {
            val recyclerView: RecyclerView = view.findViewById(R.id.rvNotifications)

            val notifications = listOf(
                Notification("New Feature", "Check out the latest features."),
                Notification("Maintenance", "Scheduled maintenance tonight."),
                Notification("Welcome", "Thanks for joining us!")
            )
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = NotificationAdapter(notifications)
        } catch (e: Exception) {
            Log.e("NotificationListFragment", "Error in onViewCreated", e)
        }
    }
}