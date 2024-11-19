package com.mlt.driver.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mlt.driver.R;
import com.mlt.driver.adapters.NotificationAdapter;
import com.mlt.driver.helper.SharedPreferencesManager;
import com.mlt.driver.models.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

        private static final String TAG = "NotificationFragment";
        private List<NotificationItem> notificationList;
        private NotificationAdapter notificationAdapter;
        private RecyclerView recyclerView;
        private LinearLayout layoutNotification;
        private Button btnClearAll;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_notification, container, false);
            // Find views
            recyclerView = view.findViewById(R.id.recReview);
            layoutNotification = view.findViewById(R.id.layoutNotification);
            btnClearAll = view.findViewById(R.id.btnClearAll);
            // Initialize RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            // Fetch notifications from SharedPreferences
            notificationList = SharedPreferencesManager.getInstance(requireContext()).getSavedNotifications();
            // Initialize the list if it's null
            if (notificationList == null) {
                notificationList = new ArrayList<>();
            }
            // Set up the adapter
            notificationAdapter = new NotificationAdapter(requireContext(), notificationList);
            recyclerView.setAdapter(notificationAdapter);
            // Update UI based on the notification list
            updateUI();
            // Clear all notifications
            btnClearAll.setOnClickListener(v -> clearAllNotifications());
            return view;
        }

        // Update UI based on the notification list
        private void updateUI() {
            if (notificationList.isEmpty()) {
                layoutNotification.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                btnClearAll.setVisibility(View.GONE);
            } else {
                layoutNotification.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                btnClearAll.setVisibility(View.VISIBLE);
            }
        }

    // Add this method to refresh the RecyclerView
    public void refreshNotifications() {
        notificationList = SharedPreferencesManager.getInstance(requireContext()).getSavedNotifications();
        if (notificationList == null) {
            notificationList = new ArrayList<>();
        }
        notificationAdapter.updateList(notificationList); // Custom method in adapter to update data
        updateUI();
    }

    // Call this in onResume to ensure updated data is displayed
    @Override
    public void onResume() {
        super.onResume();
        refreshNotifications();
    }

    // Clear all notifications
        private void clearAllNotifications() {
            notificationList.clear();
            SharedPreferencesManager.getInstance(requireContext()).clearNotifications();
            notificationAdapter.notifyDataSetChanged();
            updateUI();
        }
    }
