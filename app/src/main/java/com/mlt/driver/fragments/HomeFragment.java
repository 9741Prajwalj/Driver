package com.mlt.driver.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.mlt.driver.R;
import com.mlt.driver.activities.LoginActivity;
import com.mlt.driver.helper.SharedPreferencesManager;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker currentMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextView activeStatus;
    private Switch onlineOfflineSwitch;
    private RelativeLayout offlineLayout;
    private LocationRequest locationRequest;
    private int userId;
    private String apiToken;
    private String username;
    private DatabaseReference mDatabase;
    private SharedPreferencesManager sharedPreferencesManager;
    private LocationCallback locationCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FirebaseApp.initializeApp(requireContext());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ets.mltcorporate.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        // Initialize UI elements
        activeStatus = view.findViewById(R.id.active_Status);
        onlineOfflineSwitch = view.findViewById(R.id.online_offline_switch);
        offlineLayout = view.findViewById(R.id.offline_layout);

        mDatabase = FirebaseDatabase.getInstance().getReference("locations");

        // Initialize SharedPreferencesManager
        sharedPreferencesManager = new SharedPreferencesManager(getContext());

        if (sharedPreferencesManager.isLoggedIn()) {
            // Retrieve user data from SharedPreferences
            userId = sharedPreferencesManager.getUserId();
            username = sharedPreferencesManager.getUserName();
            apiToken = sharedPreferencesManager.getApiToken();
            String email = sharedPreferencesManager.getEmail();
            String phone = sharedPreferencesManager.getPhone();
            String address = sharedPreferencesManager.getAddress();

            // Log retrieved details for debugging
            Log.d("MapFragment", "User details retrieved - userId: " + userId + ", username: " + username + ", apiToken: " + apiToken);

        } else {
            // Log the message and show a Toast if the user is not logged in
            Log.e("MapFragment", "User not logged in");
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();

            // Optionally, navigate to the LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        // Initialize LocationRequest
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set default state to offline
        onlineOfflineSwitch.setChecked(false);
        activeStatus.setText("You're Offline");
        offlineLayout.setVisibility(View.VISIBLE);
        // Set up the online/offline switch listener
        onlineOfflineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (isInternetAvailable()) {
                    activeStatus.setText("You're Online");
                    Toast.makeText(getContext(), "Location updated", Toast.LENGTH_SHORT).show();
                    offlineLayout.setVisibility(View.GONE);
                    // Set status to "Online" (1) and update location
                    updateDriverAvailability(1);
                    updateFirebaseStatus(1);
                    // Check if location permissions are granted
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        startLocationUpdates();  // Start location updates if permission is granted
                    } else {
                        // Request permissions if not granted
                        requestLocationPermissions();
                    }
                } else {
                    onlineOfflineSwitch.setChecked(false);  // Reset switch to "off"
                    Toast.makeText(getActivity(), "Please turn on mobile data or Wi-Fi", Toast.LENGTH_SHORT).show();
                }
            } else {  // If offline
                activeStatus.setText("You're Offline");
                offlineLayout.setVisibility(View.VISIBLE);
                // Set status to "Offline" (0) and update location
                updateDriverAvailability(0);
                updateFirebaseStatus(0);  // Set status to "Offline" in Firebase

                // Disable location updates and stop sharing data
                stopLocationUpdates();  // Stop location updates
                // Clear the map and stop showing the marker
                if (googleMap != null) {
                    if (!onlineOfflineSwitch.isChecked()) {
                        if (currentMarker != null) {
                            currentMarker.remove();  // Remove the previous marker (if any)
                        }
                        return;  // If offline, don't send location to backend
                    }
                    googleMap.clear();  // Clear existing markers
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(false);  // Disable "My Location" button if permissions are granted
                    }
                }
            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.provider_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }
    private void clearLocationInFirebase() {
        if (username == null) {
            Log.e("MapFragment", "Username is null. Cannot clear location.");
            return;
        }

        // Remove the location data from Firebase when going offline
        DatabaseReference ref = mDatabase.child("Drivers").child(String.valueOf(userId));
        ref.child("location").removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "User location cleared successfully"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to clear location for username: " + username, e));
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    //on click destination address for routing
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(onlineOfflineSwitch.isChecked());
            if (onlineOfflineSwitch.isChecked()) {
                getCurrentLocation();
            }
            getCurrentLocation();
        } else {
            requestLocationPermissions();
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Initialize SharedPreferencesManager with context when fragment is attached
        sharedPreferencesManager = new SharedPreferencesManager(context);
    }
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
        // This is where you might have the Toast message causing the crash.
        if (getContext() != null) {
            Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {

        if (!onlineOfflineSwitch.isChecked()) {
            return;  // If offline, don't start location updates
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("MapFragment", "Location permissions not granted");
            return;
        }
        // LocationResult listener
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (android.location.Location location : locationResult.getLocations()) {
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();
                    // Check if the fragment is attached to an activity and context is available
                    if (isAdded() && getContext() != null) {
                        // Update marker for the current location
                        LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);
                        if (!onlineOfflineSwitch.isChecked()) {
                            return;  // If offline, don't send location to backend
                        }
                        googleMap.clear(); // Clear previous markers
                        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location")); // Add current location marker
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));

                        //location updated for every 2 sec
                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(5000);
                        // Update location in Firebase and send to backend
                        updateLocationInFirebase(currentLatitude, currentLongitude);
                    } else {
                        Log.e("LocationCallback", "Fragment is not attached, location update ignored.");
                    }
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    // Method to determine if the location is online or offline
    private boolean checkIfLocationIsOnline(double currentLatitude, double currentLongitude) {
        // Define threshold for a valid location (these thresholds may vary depending on your needs)
        double validcurrentLatitudeMin = -90.0;
        double validcurrentLatitudeMax = 90.0;
        double validcurrentLongitudeMin = -180.0;
        double validcurrentLongitudeMax = 180.0;
        // Check if the currentLatitude and currentLongitude are within the valid range
        if (currentLatitude >= validcurrentLatitudeMin && currentLatitude <= validcurrentLatitudeMax &&
                currentLongitude >= validcurrentLongitudeMin && currentLongitude <= validcurrentLongitudeMax) {
            return true; // Online if valid coordinates
        } else {
            return false; // Offline if coordinates are invalid
        }
    }
    private void updateLocationInFirebase(double currentLatitude, double currentLongitude) {
        if (!onlineOfflineSwitch.isChecked()) {
            return;  // If offline, don't update location in Firebase
        }
        if (username == null) {
            Log.e("MapFragment", "Username is null. Cannot update location.");
            return;
        }
        boolean isOnline = checkIfLocationIsOnline(currentLatitude, currentLongitude);
        int status = isOnline ? 1 : 0;  // Set 1 if online, 0 if offline
        // If the driver is online, update the location and send the data
        if (currentMarker != null) {
            currentMarker.remove();  // Remove the previous marker (if any)
        }
        // Add a new marker for the online driver
        LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);
        currentMarker = googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
        // Create a HashMap to represent the structure

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("user_id", userId);
        locationData.put("username", username);
        locationData.put("api_token", apiToken);
        locationData.put("status", status);
        Map<String, Double> location = new HashMap<>();
        location.put("currentLatitude", currentLatitude);
        location.put("currentLongitude", currentLongitude);
        locationData.put("location", location);

        // Upload to Firebase under the "Drivers" -> username structure
        DatabaseReference ref = mDatabase.child("Drivers").child(String.valueOf(userId));
        ref.setValue(locationData)
                .addOnSuccessListener(aVoid -> {
                    if (!onlineOfflineSwitch.isChecked()) {
                        return;  // If offline, don't send location to backend
                    }
                    Log.d("Firebase", "User location updated successfully");
//                    Toast.makeText(getActivity(), "Location updated in Firebase", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to update location for username: " + username, e);
                    Toast.makeText(getActivity(), "Failed to update location", Toast.LENGTH_SHORT).show();
                });

        if (isAdded() && getContext() != null) {
            getActivity().runOnUiThread(() -> {
//                Toast.makeText(getContext(), "Location updated", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("MapFragment", "Fragment is not attached or context is null");
        }
    }
    private void updateDriverAvailability(int availability) {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        String apiToken = sharedPreferencesManager.getApiToken();
        int userId = sharedPreferencesManager.getUserId();

        if (apiToken == null || userId == -1) {
            Log.e("DriverAvailabilityUpdate", "API token or user ID not found in SharedPreferences");
            return;
        }
        // Create request payload
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("api_token", apiToken);
        requestBody.put("user_id", userId);
        requestBody.put("availability", availability);

        // Create API Service
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Make API call
        apiService.updateDriverAvailability(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    int success = responseBody.get("success").getAsInt();
                    String message = responseBody.get("message").getAsString();

                    if (success == 1) {
                        Log.d("DriverAvailabilityUpdate", "Response: " + message);
                    } else {
                        Log.e("DriverAvailabilityUpdate", "Failed: " + message);
                    }
                } else {
                    Log.e("DriverAvailabilityUpdate", "Failed to update: " + response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("DriverAvailabilityUpdate", "Error updating availability", t);
            }
        });
    }
    private void updateFirebaseStatus(int status) {
        mDatabase.child("Drivers").child(String.valueOf(userId)).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "User status updated successfully"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to update status", e));
    }
    private void getCurrentLocation() {
        if (!isInternetAvailable()) {
            Toast.makeText(getActivity(), "Please turn on mobile data or Wi-Fi to see your current location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null && googleMap != null) {
                if (!onlineOfflineSwitch.isChecked()) {
                    if (currentMarker != null) {
                        currentMarker.remove();  // Remove the previous marker (if any)
                    }
                    return;  // If offline, don't send location to backend
                }
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
                updateLocationInFirebase(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(getActivity(), "Unable to find location", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //on click destination address for routing
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Log.e("MapFragment", "Location permission denied");
            }
        }
    }
        public static class Location {
            public double currentLatitude;
            public double currentLongitude;
            // No-argument constructor required for Firebase
            public Location() {
                // Default constructor required for calls to DataSnapshot.getValue(Location.class)
            }
            public Location(double currentLatitude, double currentLongitude) {
                this.currentLatitude = currentLatitude;
                this.currentLongitude = currentLongitude;
            }
        }
    }