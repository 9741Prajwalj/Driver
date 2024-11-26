package com.mlt.driver.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mlt.driver.MainActivity;
import com.mlt.driver.R;
import com.mlt.driver.models.NotificationItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationHelper extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "firebase_channel";
    private static final String CHANNEL_NAME = "Firebase Notifications";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "MyFirebaseMessaging";
    private static final String PREF_NAME = "Notifications";
    private static final String KEY_LAST_NOTIFICATION_TITLE = "last_notification_title";
    private static final String KEY_LAST_NOTIFICATION_BODY = "last_notification_body";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if the message contains custom data
        if (!remoteMessage.getData().isEmpty()) {
            String key = remoteMessage.getData().get("key");
            String rideInfo = remoteMessage.getData().get("rideinfo");
            String userDetails = remoteMessage.getData().get("user_details");
            String data = remoteMessage.getData().get("data");
            // Handle your custom data here (but don't display it as a notification)
            Log.d("FCM Data", "Key: " + key + ", RideInfo: " + rideInfo + ", UserDetails: " + userDetails);
            Log.d("FCM Data", "Hardcoded Data: " + data);
        }else {
            Log.d("FCM Data", " No Data or error in format ");
        }
        // Extract notification details
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "";
        String body = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "";
        Log.d("FCM Notification", "Title: " + title + ", Body: " + body);
        // Save notification in SharedPreferences
        saveLastNotification(title, body);

        // Create NotificationItem
        NotificationItem notificationItem = new NotificationItem(title, body);
        // Save notification in SharedPreferences
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        List<NotificationItem> notificationList = sharedPreferencesManager.getSavedNotifications();
        if (notificationList == null) {
            notificationList = new ArrayList<>();
        }
        notificationList.add(0, notificationItem); // Add new notification at the top
        sharedPreferencesManager.saveNotifications(notificationList);
        // Display notification in UI
        sendNotification(title, body);
    }
    private void sendNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Create NotificationChannel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        // Intent to open MainActivity and navigate to NotificationFragment
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("openFragment", "NotificationFragment"); // Flag to open NotificationFragment
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        // Build and show the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // Replace with your icon
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    private void saveLastNotification(String title, String body) {
        // Save the last notification title and body to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_NOTIFICATION_TITLE, title);
        editor.putString(KEY_LAST_NOTIFICATION_BODY, body);
        editor.apply();
        Log.d(TAG, "Last notification saved: Title - " + title + ", Body - " + body);
    }
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        // Save the new token to SharedPreferences
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        sharedPreferencesManager.saveDeviceToken(token);

        // Optionally send the token to your backend
        sendTokenToServer(token);

        // Log the new token for debugging
        Log.d(TAG, "New FCM token: " + token);
    }
    private void sendTokenToServer(String token) {
        Log.d(TAG, "Token sent to server: " + token);
        // Implement API call to send the token to your backend
    }
}