package com.mlt.driver.fragments;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mlt.driver.R;
import com.mlt.driver.adapters.NotificationAdapter;
import com.mlt.driver.helper.NotificationHelper;
import com.mlt.driver.helper.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private static final String PREF_NAME = "Notifications";
    private static final String KEY_LAST_NOTIFICATION_TITLE = "last_notification_title";
    private static final String KEY_LAST_NOTIFICATION_BODY = "last_notification_body";
    private static final String KEY_DEVICE_TOKEN = "device_token";
    private SharedPreferencesManager sharedPreferencesManager;
    private Context context;
    private TextView notificationTextView;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notifications> notificationList;
    private LinearLayout layoutNotification;
    private Button btnClearAll;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Find the views
        recyclerView = view.findViewById(R.id.recReview);
        layoutNotification = view.findViewById(R.id.layoutNotification);
        notificationTextView = view.findViewById(R.id.notificationTextView);
        btnClearAll = view.findViewById(R.id.btnClearAll);

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Fetch the saved notifications from SharedPreferences
        notificationList = SharedPreferencesManager.getInstance(requireContext()).getSavedNotifications();

        // Check if the list is empty and initialize it if necessary
        if (notificationList == null) {
            notificationList = new ArrayList<>();
        }

        // Initialize the adapter with the notification list
        notificationAdapter = new NotificationAdapter(requireContext(), notificationList);
        recyclerView.setAdapter(notificationAdapter);

        // Update UI based on the notification list
        updateUI();

        // Subscribe to a topic for notifications
        FirebaseMessaging.getInstance().subscribeToTopic("driver_notifications")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notificationTextView.setText("Subscribed to driver notifications.");
                    } else {
                        notificationTextView.setText("Subscription failed.");
                    }
                });

        // Fetch the device token and store it in SharedPreferences
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // Get the new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "Fetched Device Token: " + token);

                    // Save the token to SharedPreferences
                    SharedPreferencesManager.getInstance(requireContext()).saveDeviceToken(token);

                    // Optionally display the token or send to backend
                    String savedToken = SharedPreferencesManager.getInstance(requireContext()).getDeviceToken();
                    Log.d(TAG, "Saved Device Token: " + savedToken);
                });

        // Clear All Button Click Listener
        btnClearAll.setOnClickListener(v -> clearAllNotifications());

        // Fetch and display the last received notification
        loadLastNotification();

        return view;
    }

    /*** Update UI based on notification list size.*/
    private void updateUI() {
        if (notificationList.isEmpty()) {
            layoutNotification.setVisibility(View.VISIBLE); // Show "No Notification Found"
            recyclerView.setVisibility(View.GONE);
            btnClearAll.setVisibility(View.GONE);
        } else {
            layoutNotification.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.VISIBLE);
        }
    }

    /*** Save the device token to SharedPreferences.*/
    private void saveDeviceToken(String token) {
        if (getContext() != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_DEVICE_TOKEN, token);
            editor.apply();
            Log.d(TAG, "Device Token saved to SharedPreferences: " + token);
        }
    }

    /*** Send the device token to the backend server (if required).*/
    private void sendTokenToServer(String token) {
        // Implement your API call here
        Log.d(TAG, "Token sent to server: " + token);
    }

    /*** Load and display the last notification stored in SharedPreferences.*/
    private void loadNotifications() {
        // Fetch notifications from SharedPreferences
        notificationList = SharedPreferencesManager.getInstance(requireContext()).getNotifications();

        // If there are no notifications, show a message
        if (notificationList.isEmpty()) {
            layoutNotification.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.GONE);
        } else {
            layoutNotification.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.VISIBLE);
        }
        // Update RecyclerView
        notificationAdapter.notifyDataSetChanged();
        // Fetch and display the last notification if available
        loadLastNotification();
    }

    private void loadLastNotification() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String lastNotificationTitle = sharedPreferences.getString(KEY_LAST_NOTIFICATION_TITLE, "No notifications yet");
        String lastNotificationBody = sharedPreferences.getString(KEY_LAST_NOTIFICATION_BODY, "No notifications yet");

        notificationTextView.setText("Title: " + lastNotificationTitle + "\nBody: " + lastNotificationBody);
        Log.d(TAG, "Last notification loaded: Title - " + lastNotificationTitle + ", Body - " + lastNotificationBody);
    }

    // Save a new notification and update the list
    public void saveNotification(Notifications notification) {
        notificationList.add(notification);
        SharedPreferencesManager.getInstance(requireContext()).saveNotifications(notificationList);
        notificationAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void clearAllNotifications() {
        notificationList.clear();
        SharedPreferencesManager.getInstance(requireContext()).saveNotifications(notificationList);
        updateUI();// Refresh UI
    }
}
