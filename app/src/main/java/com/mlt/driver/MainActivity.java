package com.mlt.driver;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mlt.driver.activities.LoginActivity;
import com.mlt.driver.databinding.ActivityMainBinding;
import com.mlt.driver.fragments.HomeFragment;
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
    private SharedPreferencesManager sharedPreferencesManager;
    private CircleImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generateFCMToken();
        // Initialize Firebase if not already initialized
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        // Check and request permissions for Android TIRAMISU and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
        // Inflate and set up view binding
        com.mlt.driver.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        // Set up navigation components
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
        // Initialize SharedPreferencesManager
        sharedPreferencesManager = new SharedPreferencesManager(this);
        // Load profile data into NavigationView header
        loadNavigationHeaderData(navigationView);

        // Handle intents for specific fragment navigation
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
    // Check for notification permission
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationPermission", "Permission granted.");
            } else {
                Log.d("NotificationPermission", "Permission denied.");
            }
        }
    }
    // Load user profile data into NavigationView header
    private void loadNavigationHeaderData(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        imgProfile = headerView.findViewById(R.id.img_profile);
        TextView txtUserName = headerView.findViewById(R.id.txtUserName);
        TextView txtEmail = headerView.findViewById(R.id.txtEmail);

        String profileImageUrl = sharedPreferencesManager.getProfileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Picasso.get()
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.ic_dummy_user);
        }
        txtUserName.setText(sharedPreferencesManager.getUserName() != null ? sharedPreferencesManager.getUserName() : "Default User Name");
        txtEmail.setText(sharedPreferencesManager.getEmail() != null ? sharedPreferencesManager.getEmail() : "default@example.com");
    }
    // Load user profile image
    private void loadUserProfileData() {
        String profileImageUrl = sharedPreferencesManager.getProfileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Picasso.get().load(profileImageUrl).into(imgProfile);
        }
    }
    // Open NotificationFragment
    private void openNotificationFragment() {
        Fragment notificationFragment = new NotificationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, notificationFragment)
                .commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            showLogoutConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Show logout confirmation dialog
    private void showLogoutConfirmation() {
        final Dialog logoutDialog = new Dialog(this);
        logoutDialog.setContentView(R.layout.logout_confirmation);
        logoutDialog.setCancelable(false);

        Button btnCancel = logoutDialog.findViewById(R.id.btnCancel);
        Button btnConfirm = logoutDialog.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> logoutDialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            logoutDialog.dismiss();
            logoutUser();
        });
        logoutDialog.show();
    }
    // Logout user and clear data
    private void logoutUser() {
        sharedPreferencesManager.clearAllData();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    // Generate Firebase Cloud Messaging Token
    public static void generateFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCMToken", "Fetching FCM token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCMToken", "FCM Token: " + token);
                });
    }
}