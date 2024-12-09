package com.mlt.driver.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mlt.driver.R;
import com.mlt.driver.helper.SharedPreferencesManager;
import java.util.ArrayList;
import java.util.List;

public class PickupFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private LatLng currentLocation;
    private LatLng pickupLocation;
    private static final String TAG = "PickupFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private String apiToken;
    private int bookingId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pickup, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.pickup_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        // Fetch API token and booking ID from SharedPreferences
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext());
        apiToken = sharedPreferencesManager.getString("api_token", "No Token Found");
        bookingId = Integer.parseInt(sharedPreferencesManager.getString("booking_id", "0"));
        // Fetch pickup details
        fetchPickupDetails();
        return view;
    }
    private void fetchPickupDetails() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext());
        String pickupLong = sharedPreferencesManager.getString("pickup_lat", null);
        String pickupLat = sharedPreferencesManager.getString("pickup_long", null);
        if (pickupLat != null && pickupLong != null) {
            pickupLocation = new LatLng(Double.parseDouble(pickupLat), Double.parseDouble(pickupLong));
            Log.d(TAG, "Pickup Location from SharedPreferences: " + pickupLocation);
        }
    }
    private void drawRoute() {
        if (googleMap != null && currentLocation != null && pickupLocation != null) {
            // Draw a polyline between current location and pickup location with blue color
            googleMap.addPolyline(new PolylineOptions()
                    .add(currentLocation, pickupLocation)
                    .color(Color.BLUE) // Set color to blue
                    .width(8) // Set the width of the polyline
            );
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        // Check for location permissions and request if not granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        // Enable location tracking
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        // Get current location
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                // Add pickup location marker with green color
                if (pickupLocation != null) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(pickupLocation)
                            .title("Pickup Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))); // Set marker color to green
                    drawRoute();
                }
            } else {
                Log.d(TAG, "Current location is null");
            }
        });
    }
    // Method to decode a polyline string into a list of LatLng points
    public static List<LatLng> decodePolyline(String encoded) {
        List<LatLng> path = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lat += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lng += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }
        return path;
    }
}








//package com.mlt.driver.fragments;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.mlt.driver.R;
//import com.mlt.driver.helper.SharedPreferencesManager;
//import com.mlt.driver.utills.MapUtils;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//
//import java.util.List;
//
//public class PickupFragment extends Fragment implements OnMapReadyCallback {
//    private GoogleMap googleMap;
//    private LatLng currentLocation;
//    private LatLng pickupLocation;
//    private static final String TAG = "PickupFragment";
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
//    private FusedLocationProviderClient fusedLocationClient;
//    private MapUtils mapUtils; // Added for route drawing
//    private String apiToken;
//    private int bookingId;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_pickup, container, false);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//        // Initialize MapUtils for route drawing
//        mapUtils = new MapUtils(requireContext());
//        // Initialize the map
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.pickup_map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//        // Fetch API token and booking ID from SharedPreferences
//        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext());
//        apiToken = sharedPreferencesManager.getString("api_token", "No Token Found");
//        bookingId = Integer.parseInt(sharedPreferencesManager.getString("booking_id", "0"));
//        // Fetch pickup details
//        fetchPickupDetails();
//        return view;
//    }
//
//    private void fetchPickupDetails() {
//        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext());
//        String pickupLong = sharedPreferencesManager.getString("pickup_lat", null);
//        String pickupLat = sharedPreferencesManager.getString("pickup_long", null);
//        if (pickupLat != null && pickupLong != null) {
//            pickupLocation = new LatLng(Double.parseDouble(pickupLat), Double.parseDouble(pickupLong));
//            Log.d(TAG, "Pickup Location from SharedPreferences: " + pickupLocation);
//        }
//    }
//
//    @Override
//    public void onMapReady(GoogleMap map) {
//        googleMap = map;
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//        googleMap.setMyLocationEnabled(true);
//        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
//            if (location != null) {
//                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
//
//                if (pickupLocation != null) {
//                    googleMap.addMarker(new MarkerOptions()
//                            .position(pickupLocation)
//                            .title("Pickup Location")
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//
//                    // Draw the route on the map from current location to pickup location
//                    mapUtils.drawRouteToPickup(googleMap, currentLocation);
//                }
//            } else {
//                Log.d(TAG, "Current location is null");
//            }
//        });
//    }
//}