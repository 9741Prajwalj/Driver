package com.mlt.driver.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.PolyUtil;
import com.mlt.driver.R;
import com.mlt.driver.network.ApiService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupRoute {
    private final Context context;
    private final ApiService apiService;
    private final GoogleMap googleMap;
    private final String apiToken;

    public PickupRoute(Context context, ApiService apiService, GoogleMap googleMap, String apiToken) {
        this.context = context;
        this.apiService = apiService;
        this.googleMap = googleMap;
        this.apiToken = apiToken;
    }

    public void fetchPickupLocation(int bookingId, LatLng currentLocation) {
        try {
            // Create the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("api_token", apiToken);
            requestBody.put("booking_id", bookingId);

            // Make the API call
            Call<JsonObject> call = apiService.getToPickup(new JsonParser().parse(requestBody.toString()).getAsJsonObject());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject responseBody = response.body();
                        if (responseBody.get("success").getAsInt() == 1) {
                            JsonObject data = responseBody.getAsJsonObject("data");
                            String pickupLat = data.get("pickup_lat").getAsString();
                            String pickupLong = data.get("pickup_long").getAsString();
                            LatLng pickupLocation = new LatLng(Double.parseDouble(pickupLat), Double.parseDouble(pickupLong));
                            // Draw a route on the map
                            drawRoute(currentLocation, pickupLocation);
                        } else {
                            Toast.makeText(context, "Failed to fetch pickup location: " + responseBody.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Error fetching pickup location", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("PickupRoute", "API call failed", t);
                    Toast.makeText(context, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("PickupRoute", "Error creating request body", e);
        }
    }

    private void drawRoute(LatLng currentLocation, LatLng pickupLocation) {
        // Use Polyline to draw the route
        googleMap.addPolyline(new PolylineOptions()
                .add(currentLocation, pickupLocation)
                .color(context.getResources().getColor(R.color.red)) // Add your polyline color
                .width(10f));
    }

    public LatLng geocodeAddress(String address) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            Log.e("PickupRoute", "Error in geocoding", e);
        }
        return null;
    }
}

