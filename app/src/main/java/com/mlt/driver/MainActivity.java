package com.mlt.driver;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.mlt.driver.activities.LoginActivity;
import com.mlt.driver.databinding.ActivityMainBinding;
import com.mlt.driver.helper.SharedPreferencesManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

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

        loadUserProfileData();
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