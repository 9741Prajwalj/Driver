package com.mlt.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.driver.R;
import com.mlt.driver.adapters.CancelledRideAdapter;
import com.mlt.driver.adapters.CompletedRideAdapter;
import com.mlt.driver.adapters.UpcomingRideAdapter;
import com.mlt.driver.helper.SharedPreferencesManager;
import com.mlt.driver.models.CancelledRide;
import com.mlt.driver.models.CompletedRide;
import com.mlt.driver.models.UpcomingRide;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private TextView tvTitle;
    private RecyclerView recyclerView;
    private SharedPreferencesManager sharedPreferencesManager;
    private CancelledRideAdapter cancelAdapter;
    private CompletedRideAdapter completeAdapter;
    private UpcomingRideAdapter upcomingAdapter;
    private final List<UpcomingRide> rideListU = new ArrayList<>();
    private final List<CancelledRide> rideListCan = new ArrayList<>();
    private final List<CompletedRide> rideListComp = new ArrayList<>();
    private String currentView = "upcoming";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize SharedPreferencesManager
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());

        // Initialize Views
        tvTitle = view.findViewById(R.id.tvTitle);
        recyclerView = view.findViewById(R.id.recyclerView);
        Button btnCancelled = view.findViewById(R.id.btnCancelled);
        Button btnCompleted = view.findViewById(R.id.btnCompleted);
        Button btnUpcoming = view.findViewById(R.id.btnUpcoming);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        completeAdapter = new CompletedRideAdapter(getContext(), rideListComp);
        cancelAdapter = new CancelledRideAdapter(getContext(),rideListCan);
        upcomingAdapter = new UpcomingRideAdapter(getContext(),rideListU);

        recyclerView.setAdapter(upcomingAdapter);

        // Load default view (Upcoming Rides)
        loadUpcomingRides();

        // Handle Button Clicks
        btnCancelled.setOnClickListener(v -> loadCancelledRides());
        btnCompleted.setOnClickListener(v -> loadCompletedRides());
        btnUpcoming.setOnClickListener(v -> loadUpcomingRides()); // Set click listener for btnUpcoming

        return view;
    }
    private void loadUpcomingRides() {
        currentView = "upcoming";
        tvTitle.setText("Upcoming Rides");
        recyclerView.setAdapter(upcomingAdapter); // Set the upcomingAdapter when the Upcoming button is clicked
        fetchRides("upcoming");
    }
    private void loadCancelledRides() {
        currentView = "cancelled";
        tvTitle.setText("Cancelled Rides");
        recyclerView.setAdapter(cancelAdapter); // Set the cancelAdapter when the Cancelled button is clicked
        fetchRides("cancelled");
    }
    private void loadCompletedRides() {
        currentView = "completed";
        tvTitle.setText("Completed Rides");
        recyclerView.setAdapter(completeAdapter); // Set the completeAdapter when the Completed button is clicked
        fetchRides("completed");
    }
    private void fetchRides(String rideType) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ResponseBody> call = null;
        // Prepare the request body
        try {
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("api_token", sharedPreferencesManager.getApiToken());
            requestBodyJson.put("driver_id", sharedPreferencesManager.getUserId());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString());
            // Set the API call based on ride type
            switch (rideType) {
                case "upcoming":
                    call = apiService.getUpcomingRides(requestBody);
                    break;
                case "cancelled":
                    call = apiService.getCanceledRides(requestBody);
                    break;
                case "completed":
                    call = apiService.getCompletedRides(requestBody);
                    break;
            }
            // Make the API call
            if (call != null) {
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String jsonResponse = response.body().string();
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                JSONArray rideData = jsonObject.getJSONObject("data").getJSONArray(rideType + "_rides");
                                // Populate the RecyclerView based on ride type
                                switch (rideType) {
                                    case "upcoming":
                                        populateUpcomingRides(rideData);
                                        break;
                                    case "cancelled":
                                        populateCancelledRides(rideData);
                                        break;
                                    case "completed":
                                        populateCompletedRides(rideData);
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Invalid ride type");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error parsing ride data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to load rides", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error preparing request", Toast.LENGTH_SHORT).show();
        }
    }
    // Method to populate upcoming rides
    private void populateUpcomingRides(JSONArray upcomingRides) {
        try {
            rideListU.clear(); // Clear existing rides before populating new ones
            for (int i = 0; i < upcomingRides.length(); i++) {
                JSONObject ride = upcomingRides.getJSONObject(i);

                int bookingId = ride.getInt("booking_id");
                String bookDate = ride.getString("book_date");
                String bookTime = ride.getString("book_time");
                String sourceAddress = parseAddress(ride.getString("source_address"));
                String destAddress = parseAddress(ride.getString("dest_address"));
                String journeyDate = ride.getString("journey_date");
                String journeyTime = ride.getString("journey_time");
                String rideStatus = ride.getString("ride_status");
                // Create and add Ride object to the list
                UpcomingRide upcomingRide = new UpcomingRide(bookingId, bookDate, bookTime, sourceAddress, destAddress, journeyDate, journeyTime, rideStatus);
                rideListU.add(upcomingRide);
            }
            upcomingAdapter.notifyDataSetChanged(); // Notify adapter that data has changed
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error populating upcoming rides", Toast.LENGTH_SHORT).show();
        }
    }
    // Helper method to parse address
    private String parseAddress(String addressJson) {
        try {
            // Check if addressJson is a JSON Array
            if (addressJson.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(addressJson);
                return jsonArray.getString(0); // Return the first value
            }
            // Check if addressJson is a JSON Object
            else if (addressJson.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(addressJson);
                StringBuilder address = new StringBuilder();
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    address.append(jsonObject.getString(key));
                    if (keys.hasNext()) address.append(", "); // Add a comma between values
                }
                return address.toString();
            }
            // Treat as plain String otherwise
            return addressJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return "Invalid address format";
        }
    }
    // Method to populate cancelled rides
    private void populateCancelledRides(JSONArray cancelledRides) {
        try {
            rideListCan.clear(); // Clear existing rides before populating new ones
            for (int i = 0; i < cancelledRides.length(); i++) {
                JSONObject ride = cancelledRides.getJSONObject(i);

                // Extract values from the JSON object
                int bookingId = ride.getInt("booking_id");
                String bookDate = ride.getString("book_date");
                String bookTime = ride.getString("book_time");
                String sourceAddress = ride.getString("source_address"); // Changed to getString()
                String destAddress = ride.getString("dest_address"); // Changed to getString()
                String journeyDate = ride.getString("journey_date");
                String journeyTime = ride.getString("journey_time");
                String rideStatus = ride.getString("ride_status");
                // Create and add Ride object to the list
                CancelledRide cancelledRide = new CancelledRide(bookingId, bookDate, bookTime, sourceAddress, destAddress, journeyDate, journeyTime, rideStatus);
                rideListCan.add(cancelledRide);
            }
            cancelAdapter.notifyDataSetChanged(); // Notify adapter that data has changed
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error populating cancelled rides", Toast.LENGTH_SHORT).show();
        }
    }
    // Method to populate completed rides
    private void populateCompletedRides(JSONArray completedRides) {
        try {
            rideListComp.clear(); // Clear existing rides before populating new ones
            for (int i = 0; i < completedRides.length(); i++) {
                JSONObject ride = completedRides.getJSONObject(i);

                int bookingId = ride.getInt("booking_id");
                String bookDate = ride.getString("book_date");
                String journeyDate = ride.getString("journey_date");
                String sourceAddress = parseAddress(ride.getString("source_address"));
                String destAddress = parseAddress(ride.getString("dest_address"));
                String rideStatus = ride.getString("ride_status");
                String sourceTime = ride.optString("source_time", "N/A");
                String destTime = ride.optString("dest_time", "N/A");
                String totalKms = ride.optString("total_kms", "N/A");
                int amount = ride.optInt("amount", 0);
                // Create and add CompletedRide object to the list
                CompletedRide completedRide = new CompletedRide(
                        bookingId, bookDate, journeyDate, sourceAddress, destAddress,
                        rideStatus, sourceTime, destTime, totalKms, amount
                );
                rideListComp.add(completedRide);
            }
            completeAdapter.notifyDataSetChanged(); // Notify adapter that data has changed
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error populating completed rides", Toast.LENGTH_SHORT).show();
        }
    }
}