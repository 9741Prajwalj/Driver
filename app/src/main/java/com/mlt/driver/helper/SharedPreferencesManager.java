package com.mlt.driver.helper;

/**
 * Created by Prajwal J.
 */

import static android.content.ContentValues.TAG;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mlt.driver.models.NotificationItem;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager {

    public static final String PREF_NAME = "user_prefs";
    private static final String KEY_PROFILE_IMAGE_URL = "profile_image_url";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID = "user_id";
    private static final String KEY_API_TOKEN = "api_token";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_STATUS = "status";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_DEVICE_TOKEN = "device_token"; // For Firebase token
    private static SharedPreferencesManager instance; // Singleton instance
    public static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String Tag = "SharedPreferencesManager";
    private static final String KEY_NOTIFICATIONS = "notification_list";

    public SharedPreferencesManager(Context context) {
        if (context != null) {
            // Initialize SharedPreferences using the context
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            this.editor = sharedPreferences.edit();
        } else {
            throw new IllegalArgumentException("Context cannot be null");
        }
    }
    public SharedPreferencesManager(int userId, int status, double latitude, double longitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putInt(KEY_STATUS, status);
        editor.putFloat(KEY_LATITUDE, (float) latitude);
        editor.putFloat(KEY_LONGITUDE, (float) longitude);
        editor.apply();
    }
    public boolean isLoggedIn() {
        if (sharedPreferences != null) {
            return sharedPreferences.contains(KEY_USER_ID) && sharedPreferences.contains(KEY_API_TOKEN ) && sharedPreferences.contains(KEY_USER_NAME) && sharedPreferences.contains(KEY_EMAIL) &&
                    sharedPreferences.contains(KEY_PHONE) && sharedPreferences.contains(KEY_ADDRESS);
        }
        return false;
    }
    // Save login data
    public void saveLoginData(int userId, String username, String apiToken, String email, String phone, String address, boolean isLoggedIn, int status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, username);
        editor.putString(KEY_API_TOKEN, apiToken);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.putInt(KEY_STATUS, status);
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
        // Log the saved values
        Log.d(Tag, "Saved User ID: " + userId);
        Log.d(Tag, "Saved Username: " + username);
        Log.d(Tag, "Saved API Token: " + apiToken);
        Log.d(Tag, "Saved Login Status: " + isLoggedIn);
    }
    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
    // Add methods in SharedPreferencesManager
    public void saveLatitude(double lat) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("latitude", Double.doubleToRawLongBits(lat));
        editor.apply();
    }
    public double getLatitude() {
        return Double.longBitsToDouble(sharedPreferences.getLong("latitude", Double.doubleToRawLongBits(0.0)));
    }
    public void saveLongitude(double lng) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("longitude", Double.doubleToRawLongBits(lng));
        editor.apply();
    }
    public double getLongitude() {
        return Double.longBitsToDouble(sharedPreferences.getLong("longitude", Double.doubleToRawLongBits(0.0)));
    }
    public void savePickupLat(double lat) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("pickupLat", Double.doubleToRawLongBits(lat));
        editor.apply();
    }
    public double getPickupLat() {
        return Double.longBitsToDouble(sharedPreferences.getLong("pickupLat", Double.doubleToRawLongBits(0.0)));
    }

    public void savePickupLong(double lng) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("pickupLong", Double.doubleToRawLongBits(lng));
        editor.apply();
    }

    public double getPickupLong() {
        return Double.longBitsToDouble(sharedPreferences.getLong("pickupLong", Double.doubleToRawLongBits(0.0)));
    }

    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }
    // Clear login data
    public void clearLoginData() {
        if (sharedPreferences != null) {
            // Set the user status to offline (0) before clearing data
            int status = 0;
            updateUserStatus(getUserId(), status);
            // Clear login-related data
            editor.remove(KEY_USER_ID);
            editor.remove(KEY_USER_NAME);
            editor.remove(KEY_API_TOKEN);
            editor.remove(KEY_IS_LOGGED_IN);
            editor.apply();

            Log.d(Tag, "User logged out and data cleared");
        } else {
            Log.e(Tag, "SharedPreferences is null");
        }
    }
    // Public method to get the singleton instance
    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext()); // Use application context
        }
        return instance;
    }
    // Save Firebase device token
    public void saveDeviceToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DEVICE_TOKEN, token);
        editor.apply();
        Log.d("SharedPreferencesManager", "Firebase device token saved: " + token);
    }
    // Retrieve Firebase device token
    public String getDeviceToken() {
        String token = sharedPreferences.getString(KEY_DEVICE_TOKEN, null);
        Log.d("SharedPreferencesManager", "Fetched device token: " + token);
        return token;
    }
    public List<NotificationItem> getSavedNotifications() {
        String json = sharedPreferences.getString(KEY_NOTIFICATIONS, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<NotificationItem>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }
    // Save notifications list to SharedPreferences
    public void saveNotifications(List<NotificationItem> notificationList) {
        Gson gson = new Gson();
        String json = gson.toJson(notificationList);
        sharedPreferences.edit().putString(KEY_NOTIFICATIONS, json).apply();
    }
    // Retrieve notifications list from SharedPreferences
    public List<NotificationItem> getNotifications() {
        String json = sharedPreferences.getString(KEY_NOTIFICATIONS, null);
        if (json != null) {
            Type type = new TypeToken<List<NotificationItem>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }
    public void clearNotifications() {
        sharedPreferences.edit().remove(KEY_NOTIFICATIONS).apply();
    }
    // Update user status in the backend (e.g., online/offline)
    private void updateUserStatus(int userId, int status) {
        // Implement API call to update user status in the backend
        Log.d(TAG, "User status updated to: " + status + " for User ID: " + userId);
    }
    public void saveProfileImageUrl(String imageUrl) {
        editor.putString(KEY_PROFILE_IMAGE_URL, imageUrl);
        editor.apply();
    }
    public String getProfileImageUrl() {
        return sharedPreferences.getString(KEY_PROFILE_IMAGE_URL, "");
    }
    // Get login data
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }
    public String getApiToken() {
        return sharedPreferences.getString(KEY_API_TOKEN, "");
    }
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }
    public  String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }
    public  String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, "");
    }
    public  String getAddress() {
        return sharedPreferences.getString(KEY_ADDRESS, "");
    }
    public void setDriverStatus(int status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("driver_status", status); // 1 for online, 0 for offline
        editor.apply();
    }
    public boolean getDriverStatus() {
        return sharedPreferences.getInt("driver_status", 0) == 1; // Default to offline
    }
    // Clear all stored data
    public void clearAllData() {
        // Fetch the device token before clearing other data
        String deviceToken = sharedPreferences.getString(KEY_DEVICE_TOKEN, null);
        // Clear all preferences except for the device token
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Restore the device token if it was present
        if (deviceToken != null) {
            saveDeviceToken(deviceToken);
        }
        Log.d(TAG, "All shared preferences cleared except device token");
    }
}