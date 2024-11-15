package com.mlt.driver.helper;

//import static com.mlt.driver.manager.UserStatusManager.updateStatus;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "user_prefs";

    private static final String KEY_PROFILE_IMAGE_URL = "profile_image_url";
    // Login Data Keys
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

    private static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String Tag ="SharedPreferencesManager";

    public SharedPreferencesManager(Context context) {
        if (context != null) {
            // Initialize SharedPreferences using the context
            this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
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
        editor.putInt("USER_STATUS", status);
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

    // Clear login data
    public void clearLoginData() {
        if (sharedPreferences != null) {
            // Set the user status to offline (0) before clearing data
            int status = 0;
            int userId = 0;
//            updateStatus(userId,status);

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
        return sharedPreferences.getString(KEY_USER_NAME, "me");
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



    // Clear all stored data
    public void clearAllData() {
        editor.clear();
        editor.apply();
    }
}

