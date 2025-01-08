package com.emotion.detector.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emotion.detector.R
import com.emotion.detector.adapter.NotificationAdapter

class NotificationListFragment : Fragment() {

    private val notificationViewModel: NotificationViewModel by viewModels()

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
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Observe notifications from the ViewModel
            notificationViewModel.notifications.observe(viewLifecycleOwner, Observer { notifications ->
                if (notifications != null) {
                    recyclerView.adapter = NotificationAdapter(notifications)
                } else {
                    Log.d("NotificationListFragment", "No notifications found")
                }
            })
        } catch (e: Exception) {
            Log.e("NotificationListFragment", "Error in onViewCreated", e)
        }
    }
}
