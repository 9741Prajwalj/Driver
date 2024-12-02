package com.mlt.driver.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mlt.driver.MainActivity;
import com.mlt.driver.R;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;
import com.mlt.driver.utills.MyEditText;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.mlt.driver.helper.SharedPreferencesManager;


public class LoginActivity extends AppCompatActivity {

    private static final String KEY_DEVICE_TOKEN = "device_token";
    private MyEditText etEmail;
    private MyEditText etPassword;
    private TextView btnLogin;
    private TextView txtSignUp;
    private int userId;
    private String username;
    private String apiToken;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MainActivity", "Logout and came to the login activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        // Check for login status
        if (sharedPreferencesManager.isLoggedIn()) {
            onLoginSuccess();
            finish();
            return;
        }

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Fetch Firebase device token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String deviceToken = task.getResult();
                        sharedPreferencesManager.saveDeviceToken(deviceToken); // Save the token
                        Log.d("FirebaseToken", "Fetched device token: " + deviceToken);
                    } else {
                        Log.e("FirebaseToken", "Failed to fetch device token", task.getException());
                    }
                });

        btnLogin.setOnClickListener(view -> {
            if (validateInput()) {
                loginUser();
            }
        });
    }
    //For static use
//        private void loginUser () {
//            // Example logic for login validation and success
//            boolean isLoginSuccessful = true; // Replace with actual login logic
//
//            if (isLoginSuccessful) {
//                onLoginSuccess(this);
//            }
//        }

    private boolean validateInput() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Retrieve the FCM ID from SharedPreferences
        String fcmId = sharedPreferencesManager.getDeviceToken();
        Log.d("FCM_id", "FCM ID from SharedPreferences: " + fcmId);

        if (fcmId == null) {
            Log.e("FCM_id", "FCM ID is not available.");
            Toast.makeText(this, "FCM ID is missing", Toast.LENGTH_SHORT).show();
            return; // Stop the login process if FCM ID is missing
        }

        JSONObject loginRequest = new JSONObject();
        try {
            loginRequest.put("fcm_id", fcmId);
            loginRequest.put("username", email);
            loginRequest.put("password", password);
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), loginRequest.toString());
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.loginUser(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        int success = jsonResponse.optInt("success", 0);
                        Log.d("LoginActivity", "Response success flag: " + success);

                        if (success == 1) {
                            JSONObject dataObject = jsonResponse.getJSONObject("data").getJSONObject("userinfo");
                            int userId = dataObject.optInt("user_id", -1);
                            String apiToken = dataObject.optString("api_token");
                            String userName = dataObject.optString("user_name");
                            String email = dataObject.optString("emailid");
                            String phone = dataObject.optString("phone");
                            String address = dataObject.optString("address", "Address not available");
                            int status = dataObject.optInt("status", 0);

                            if (userId != -1 && !userName.isEmpty() && !apiToken.isEmpty()) {
                                sharedPreferencesManager.saveLoginData(userId, userName, apiToken, email, phone, address, true, status);
                                Log.d("LoginActivity", "User data saved in SharedPreferences");

                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                onLoginSuccess();
                            } else {
                                Log.e("LoginActivity", "Invalid user data received - Missing userId, apiToken, or userName");
                                Toast.makeText(LoginActivity.this, "Invalid user data received", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = jsonResponse.optString("message", "Login failed");
                            Log.e("LoginActivity", "Login failed - Message from server: " + message);
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("LoginActivity", "Error parsing response", e);
                        Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("LoginActivity", "Login failed - Response not successful or body is null");
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LoginActivity", "Login request failed - Error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLoginSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //For Static use
//        private static void onLoginSuccess(Context context) {
//            if (context == null) {
//                Log.e("LoginActivity", "Context is null in onLoginSuccess");
//                return; // Prevent further execution if the context is null
//            }
//
//            Intent intent = new Intent(context, MainActivity.class);
//            context.startActivity(intent);
//
//            // Finish the activity if the context is an instance of Activity
//            if (context instanceof Activity) {
//                ((Activity) context).finish();
//            }
//        }
}