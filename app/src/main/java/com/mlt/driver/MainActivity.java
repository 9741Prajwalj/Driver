package com.mlt.driver;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.mlt.driver.activities.LoginActivity;
import com.mlt.driver.databinding.ActivityMainBinding;
import com.mlt.driver.fragments.NotificationFragment;
import com.mlt.driver.helper.SharedPreferencesManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import android.Manifest;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SharedPreferencesManager sharedPreferencesManager;
    private CircleImageView imgProfile;
    private TextView txtUserName, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS}, 1);
            }
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profile, R.id.nav_home, R.id.nav_history, R.id.nav_earning,
                R.id.nav_notification, R.id.nav_review, R.id.nav_Help, R.id.nav_summary)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        String profileImageUrl = sharedPreferencesManager.getProfileImageUrl();
        // Access views in NavigationView header
        View headerView = navigationView.getHeaderView(0);
        imgProfile = headerView.findViewById(R.id.img_profile);
        txtUserName = headerView.findViewById(R.id.txtUserName);
        txtEmail = headerView.findViewById(R.id.txtEmail);

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Picasso.get()
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_dummy_user) // Fallback image resource
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.ic_dummy_user); // Default image if URL is null or empty
        }
        txtUserName.setText(sharedPreferencesManager.getUserName() != null ? sharedPreferencesManager.getUserName() : "Default User Name");
        txtEmail.setText(sharedPreferencesManager.getEmail() != null ? sharedPreferencesManager.getEmail() : "default@example.com");

        // Ensure the correct flag is read in onCreate
        if (getIntent().hasExtra("openFragment")) {
            String fragmentName = getIntent().getStringExtra("openFragment");
            if ("NotificationFragment".equals(fragmentName)) {
                openNotificationFragment();
            }
        }
        loadUserProfileData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        // Check if the OS version requires notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                Log.d("NotificationPermission", "Permission already granted.");
                // Proceed with showing notifications
            }
        } else {
            Log.d("NotificationPermission", "Permission not required for this OS version.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationPermission", "Permission granted.");
                // Proceed with showing notifications
            } else {
                Log.d("NotificationPermission", "Permission denied.");
                // Handle the case when permission is denied
            }
        }
    }
    private void loadUserProfileData() {
        // Get user profile image URL from SharedPreferences
        String profileImageUrl = sharedPreferencesManager.getProfileImageUrl();

        // If a profile image URL exists, load it into the CircleImageView
        if (!profileImageUrl.isEmpty()) {
            // You can use an image loading library like Picasso or Glide to load the image
            Picasso.get().load(profileImageUrl).into(imgProfile); // Example using Picasso
        }
    }
    private void openNotificationFragment() {
        Fragment notificationFragment = new NotificationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, notificationFragment) // Replace with your container ID
                .commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    // Handle logout action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            showLogoutConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Method to show logout confirmation dialog
    private void showLogoutConfirmation() {
        final Dialog logoutDialog = new Dialog(this);
        logoutDialog.setContentView(R.layout.logout_confirmation);
        logoutDialog.setCancelable(false);

        Button btnCancel = logoutDialog.findViewById(R.id.btnCancel);
        Button btnConfirm = logoutDialog.findViewById(R.id.btnConfirm);

        // Set cancel button action
        btnCancel.setOnClickListener(v -> logoutDialog.dismiss());

        // Set confirm button action
        btnConfirm.setOnClickListener(v -> {
            logoutDialog.dismiss();
            logoutUser(); // Call the logoutUser method to clear data and navigate to LoginActivity
        });
        logoutDialog.show();
    }
    private void logoutUser() {
        SharedPreferencesManager.getInstance(this).clearNotifications();
        // Clear all data in SharedPreferences
        sharedPreferencesManager.clearAllData();
        // Navigate to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // End MainActivity
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}