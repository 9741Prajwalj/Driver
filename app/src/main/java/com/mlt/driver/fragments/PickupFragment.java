package com.mlt.driver.fragments;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mlt.driver.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PickupFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap; // Removed static
    private LatLng currentLocation;
    private LatLng pickupLocation;

    private static final String DIRECTIONS_API_KEY = "AIzaSyCI7CwlYJ6Qt5pQGW--inSsJmdEManW-K0";
    // Add an interface to handle back press event

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pickup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the map fragment and set the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.pickup_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        // Simulate current location and pickup location for demonstration purposes.
        currentLocation = new LatLng(13.0604819, 77.5018494); // Example: Current location
        pickupLocation = new LatLng(12.3301, 76.6152); // Example: Pickup location

        // Set up the back press listener
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to HomeFragment
                navigateToHomeFragment();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (currentLocation != null && pickupLocation != null) {
            // Determine the southwest and northeast bounds.
            double minLat = Math.min(currentLocation.latitude, pickupLocation.latitude);
            double maxLat = Math.max(currentLocation.latitude, pickupLocation.latitude);
            double minLng = Math.min(currentLocation.longitude, pickupLocation.longitude);
            double maxLng = Math.max(currentLocation.longitude, pickupLocation.longitude);

            LatLngBounds bounds = new LatLngBounds(
                    new LatLng(minLat, minLng), // Southwest corner
                    new LatLng(maxLat, maxLng)  // Northeast corner
            );

            // Move the camera to show the bounds
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            // Add markers for current location and pickup location
            googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            // Fetch route data from the Directions API and draw the route.
            fetchRoute(currentLocation, pickupLocation);
        }
    }

    private void fetchRoute(LatLng origin, LatLng destination) {
        String originStr = origin.latitude + "," + origin.longitude;
        String destinationStr = destination.latitude + "," + destination.longitude;
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + originStr +
                "&destination=" + destinationStr +
                "&key=" + DIRECTIONS_API_KEY;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("PickupFragment", "Failed to fetch route: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d("PickupFragment", "Response: " + responseBody);

                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String encodedPolyline = overviewPolyline.getString("points");

                            List<LatLng> path = decodePolyline(encodedPolyline);
                            Log.d("PickupFragment", "Path size: " + path.size());

                            if (path.size() > 1) {
                                // Update the UI on the main thread
                                getActivity().runOnUiThread(() -> {
                                    int midpoint = path.size() / 2;
                                    List<LatLng> firstHalf = path.subList(0, midpoint);
                                    List<LatLng> secondHalf = path.subList(midpoint, path.size());

                                    // Add the first half with a red color
                                    googleMap.addPolyline(new PolylineOptions()
                                            .addAll(firstHalf)
                                            .width(10)
                                            .color(Color.BLUE)
                                            .geodesic(true));

                                    // Add the second half with a green color
                                    googleMap.addPolyline(new PolylineOptions()
                                            .addAll(secondHalf)
                                            .width(10)
                                            .color(Color.BLUE)
                                            .geodesic(true));
                                });
                            }
                        } else {
                            Log.e("PickupFragment", "No routes found in response.");
                        }
                    } catch (Exception e) {
                        Log.e("PickupFragment", "Error parsing route data: " + e.getMessage());
                    }
                } else {
                    Log.e("PickupFragment", "Error response: " + (response.body() != null ? response.body().string() : "No response body"));
                }
            }
        });
    }
    private List<LatLng> decodePolyline(String encoded) {
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
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            path.add(new LatLng((lat / 1E5), (lng / 1E5)));
        }
        return path;
    }
    private void navigateToHomeFragment() {
        // Navigate to HomeFragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container12, new HomeFragment());
        transaction.addToBackStack(null); // Optional: add to back stack if you want to maintain history
        transaction.commit();
    }
}



//public class PickupFragment extends Fragment implements OnMapReadyCallback {
//    private static final String KEY_API_TOKEN = "N8pBvNaYHtEGLTEYrCW9KZsqDntoNI2BiMWG1Dj6UADR9dUPjkhXHMcA24pU";
//    // N8pBvNaYHtEGLTEYrCW9KZsqDntoNI2BiMWG1Dj6UADR9dUPjkhXHMcA24pU
//    private GoogleMap googleMap;
//    private SharedPreferences sharedPreferences;
//    private String apiToken;
//    private LatLng currentLocation;
//    private LatLng pickupLocation;
//    private FusedLocationProviderClient fusedLocationClient;
//    // Define the base URL for the Retrofit instance
//    private static final String BASE_URL = "https://ets.mltcorporate.com"; // Replace with your actual backend URL
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_pickup, container, false);
//    }
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        // Retrieve the API token from SharedPreferences
//        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, getContext().MODE_PRIVATE);
//        apiToken = sharedPreferences.getString(KEY_API_TOKEN, "");
//        // Initialize FusedLocationProviderClient for getting the current location
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
//        // Request the current location
//        getCurrentLocation();
//        // Make a request to the backend to get the pickup address
//        getPickupAddress();
//        // Obtain the SupportMapFragment and register for the map callback.
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.pickup_map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//    }
//    private void getCurrentLocation() {
//        // Check for location permissions and request the current location
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(location -> {
//                        if (location != null) {
//                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                            Log.d("PickupFragment", "Current location: " + currentLocation);
//                            if (googleMap != null) {
//                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
//                                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location")
//                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))); // Green marker
//                            }
//                        }
//                    })
//                    .addOnFailureListener(e -> Log.e("PickupFragment", "Failed to get current location: " + e.getMessage()));
//        } else {
//            Log.e("PickupFragment", "Location permission not granted");
//            // Optionally, request permissions here if needed
//        }
//    }
//    private void getPickupAddress() {
//        // Create Retrofit instance with lenient Gson converter
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
//                .build();
//        ApiService apiService = retrofit.create(ApiService.class);
//        PickupRequest pickupRequest = new PickupRequest(apiToken, 367); // 367 is your booking ID
//        Call<PickupResponse> call = apiService.getPickupAddress(pickupRequest);
//        call.enqueue(new Callback<PickupResponse>() {
//            @Override
//            public void onResponse(Call<PickupResponse> call, Response<PickupResponse> response) {
//                if (response.isSuccessful()) {
//                    try {
//                        // Log the raw response to check the format
//                        String rawResponse = response.errorBody() != null ? response.errorBody().string() : "No raw response body";
//                        Log.d("PickupFragment", "Raw response: " + rawResponse);
//
//                        if (response.body() != null) {
//                            PickupResponse pickupResponse = response.body();
//                            String pickupAddress = pickupResponse.getData().getPickupAddress();
//                            Log.d("PickupFragment", "Pickup address: " + pickupAddress);
//                            convertAddressToLatLng(pickupAddress);
//                        } else {
//                            Log.e("PickupFragment", "Response body is null");
//                        }
//                    } catch (IOException e) {
//                        Log.e("PickupFragment", "Error reading raw response: " + e.getMessage());
//                    }
//                } else {
//                    Log.e("PickupFragment", "Error response: " + response.errorBody());
//                }
//            }
//            @Override
//            public void onFailure(Call<PickupResponse> call, Throwable t) {
//                Log.e("PickupFragment", "Error: " + t.getMessage());
//            }
//        });
//    }
//    private void convertAddressToLatLng(String address) {
//        Geocoder geocoder = new Geocoder(getContext());
//        try {
//            List<Address> addresses = geocoder.getFromLocationName(address, 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                Address location = addresses.get(0);
//                pickupLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                Log.d("PickupFragment", "Pickup location: " + pickupLocation);
//                if (googleMap != null) {
//                    // Update map with the current and pickup locations
//                    googleMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Location")
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))); // Red marker
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
//
//                    // Draw a polyline between current and pickup locations
//                    googleMap.addPolyline(new PolylineOptions()
//                            .add(currentLocation, pickupLocation)
//                            .width(5)
//                            .color(0xFF0000FF) // Blue color for the route
//                            .geodesic(true));
//                }
//            } else {
//                Log.e("PickupFragment", "No location found for the address");
//            }
//        } catch (IOException e) {
//            Log.e("PickupFragment", "Geocoding error: " + e.getMessage());
//        }
//    }
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        this.googleMap = googleMap;
//        if (currentLocation != null) {
//            // Set up the current location on the map if it's available
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
//            googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))); // Green marker
//        }
//    }
//}

