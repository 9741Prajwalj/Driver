package com.mlt.driver.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mlt.driver.R;
import com.mlt.driver.activities.LoginActivity;
import com.mlt.driver.helper.SharedPreferencesManager;
import com.mlt.driver.models.DriverLocationRequest;
import com.mlt.driver.models.DriverStatusRequest;
import com.mlt.driver.network.ApiService;

import java.util.ArrayList;
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
    private ImageView menuIcon;
    private TextView activeStatus;
    private Switch onlineOfflineSwitch;
    private RelativeLayout offlineLayout;
    private LocationRequest locationRequest;
    private ApiService apiService;
    private int userId;
    private String userType;
    private String apiToken;
    private String username;
    private String userName;
    private String email;
    private String address;
    private String phone;
    private DatabaseReference mDatabase;
    private SharedPreferencesManager sharedPreferencesManager;
    private static SharedPreferences sharedPreferences;
    private LocationCallback locationCallback;
    private Context context;

    private LatLng destinationLocation, currentLatLng;
    private ArrayList<Polyline> polyline = null;
    private AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FirebaseApp.initializeApp(requireContext());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ets.mltcorporate.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);


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
            email = sharedPreferencesManager.getEmail();
            phone = sharedPreferencesManager.getPhone();
            address = sharedPreferencesManager.getAddress();

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
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);
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
                    sendDriverStatusToBackend(1);  // Set status to "Online" (1)
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
                sendDriverStatusToBackend(0);  // Set status to "Offline" (0)
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
        GoogleMap map = googleMap;
        this.googleMap = map;

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
        if (context != null) {
            sharedPreferencesManager = new SharedPreferencesManager(context);
        }
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
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // Check if the fragment is attached to an activity and context is available
                    if (isAdded() && getContext() != null) {
                        // Update marker for the current location
                        LatLng currentLatLng = new LatLng(latitude, longitude);
                        if (!onlineOfflineSwitch.isChecked()) {
                            return;  // If offline, don't send location to backend
                        }
                        googleMap.clear(); // Clear previous markers
                        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location")); // Add current location marker
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));

                        //location updated for every 2 sec
                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(2000);

                        // Update location in Firebase and send to backend
                        updateLocationInFirebase(latitude, longitude);
                        sendLocationToBackend(latitude, longitude);
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
    private boolean checkIfLocationIsOnline(double latitude, double longitude) {
        // Define threshold for a valid location (these thresholds may vary depending on your needs)
        double validLatitudeMin = -90.0;
        double validLatitudeMax = 90.0;
        double validLongitudeMin = -180.0;
        double validLongitudeMax = 180.0;

        // Check if the latitude and longitude are within the valid range
        if (latitude >= validLatitudeMin && latitude <= validLatitudeMax &&
                longitude >= validLongitudeMin && longitude <= validLongitudeMax) {
            return true; // Online if valid coordinates
        } else {
            return false; // Offline if coordinates are invalid
        }
    }

    private void updateLocationInFirebase(double latitude, double longitude) {
        if (!onlineOfflineSwitch.isChecked()) {
            return;  // If offline, don't update location in Firebase
        }
        if (username == null) {
            Log.e("MapFragment", "Username is null. Cannot update location.");
            return;
        }

        boolean isOnline = checkIfLocationIsOnline(latitude, longitude);
        int status = isOnline ? 1 : 0;  // Set 1 if online, 0 if offline

        // If the driver is online, update the location and send the data
        if (currentMarker != null) {
            currentMarker.remove();  // Remove the previous marker (if any)
        }

        // Add a new marker for the online driver
        LatLng currentLatLng = new LatLng(latitude, longitude);
        currentMarker = googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
        // Create a HashMap to represent the structure

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("user_id", userId);
        locationData.put("username", username);
        locationData.put("api_token", apiToken);
        locationData.put("status", status);
        Map<String, Double> location = new HashMap<>();
        location.put("latitude", latitude);
        location.put("longitude", longitude);
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
    // Method to send driver status to the backend
    private void sendDriverStatusToBackend(int status) {
//        if (!onlineOfflineSwitch.isChecked()) {
//            return;  // If offline, don't send location to backend
//        }
        // Retrieve API token and user ID from SharedPreferences
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String apiToken = sharedPreferencesManager.getApiToken();
        int userId = sharedPreferencesManager.getUserId();
        String username = sharedPreferencesManager.getUserName();

        // Check if the fragment is still attached to its activity
        if (!isAdded() || getContext() == null || sharedPreferencesManager == null) {
            Log.e("sendDriverStatusToBackend", "Fragment not attached. Cannot send location.");
            return;
        }
        // Validate API token and user ID
        if (apiToken == null || userId == -1) {
            Log.e("sendDriverStatusToBackend", "API token or user ID not found in SharedPreferences");
            return;
        }

        DriverStatusRequest statusRequest = new DriverStatusRequest(userId, status);

        // Make the API request to update driver status
        apiService.updateDriverStatus("Bearer " + apiToken, statusRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("sendDriverStatusToBackend", "Driver status updated successfully");
                } else {
                    Log.e("sendDriverStatusToBackend", "Failed to update status: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("sendDriverStatusToBackend", "Error updating driver status", t);
            }
        });
    }
    private void sendLocationToBackend(double latitude, double longitude) {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        String apiToken = sharedPreferencesManager.getApiToken();
        int userId = sharedPreferencesManager.getUserId();
        String username = sharedPreferencesManager.getUserName();
        if (getContext() == null || sharedPreferencesManager == null) {
            Log.e("MapFragment", "Context or SharedPreferencesManager is null");
            return;
        }
        if (apiToken == null || userId == -1 || username == null) {
            Log.e("sendLocationToBackend", "API token, user ID, or username not found in SharedPreferences");
            return;
        }

        DriverLocationRequest locationRequest = new DriverLocationRequest(userId, latitude, longitude);

        apiService.updateDriverLocation("Bearer " + apiToken, locationRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (!onlineOfflineSwitch.isChecked()) {
                        return;  // If offline, don't send location to backend
                    }
                    Log.d("sendLocationToBackend", "Location updated successfully");
                } else {
                    Log.e("sendLocationToBackend", "Failed to update location: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("sendLocationToBackend", "Error updating location", t);
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
                sendLocationToBackend(location.getLatitude(), location.getLongitude());
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

    public static class LocationData {
        public String user_id;
        public String username;
        public Location location;
        // No-argument constructor required for Firebase
        public LocationData() {
            // Default constructor required for calls to DataSnapshot.getValue(LocationData.class)
        }
        public LocationData(double latitude, double longitude,String username, int userId) {
            this.user_id = String.valueOf(userId);
            this.username = username;
            this.location = new Location(latitude, longitude);
        }
        public static class Location {
            public double latitude;
            public double longitude;
            // No-argument constructor required for Firebase
            public Location() {
                // Default constructor required for calls to DataSnapshot.getValue(Location.class)
            }
            public Location(double latitude, double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }
        }
    }
}