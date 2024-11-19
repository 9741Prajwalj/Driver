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

        // Extract notification details
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "";
        String body = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "";

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
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New Device Token: " + token);
        // Save the new token to SharedPreferences
        SharedPreferencesManager.getInstance(getApplicationContext()).saveDeviceToken(token);
        Log.d(TAG, "Device Token saved to SharedPreferences: " + token);
        // Optionally send the token to your backend
        sendTokenToServer(token);
    }
    private void sendTokenToServer(String token) {
        Log.d(TAG, "Token sent to server: " + token);
        // Implement API call to send the token to your backend
    }
}


