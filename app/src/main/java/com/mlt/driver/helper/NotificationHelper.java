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
import com.mlt.driver.fragments.NotificationFragment; // Replace with your main activity or target activity
import com.mlt.driver.R; // Replace with your app's R file
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        saveLastNotification(title, body); // Save notification data
        sendNotification(title, body);

        // Check if the message contains data payload
        if (!remoteMessage.getData().isEmpty()) {
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
        }
        // Check if the message contains notification payload
        else if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        if (title != null && body != null) {
            saveLastNotification(title, body); // Save notification to SharedPreferences
            sendNotification(title, body); // Display notification
        }
    }

    private void sendNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create NotificationChannel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an Intent to open NotificationFragment when the notification is clicked
        Intent intent = new Intent(this, NotificationFragment.class); // Adjust this to target your fragment
        intent.putExtra("openFragment", "NotificationFragment"); // Pass fragment identifier
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void saveLastNotification(String title, String body) {
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

        // Use getApplicationContext() to access SharedPreferencesManager
        String deviceToken = SharedPreferencesManager.getInstance(getApplicationContext()).getDeviceToken();
        Log.d("DeviceToken", "Retrieved Token: " + deviceToken);

        Log.d(TAG, "Device Token: " + token);

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


