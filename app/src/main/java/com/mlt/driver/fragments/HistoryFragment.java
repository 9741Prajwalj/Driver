package com.mlt.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.driver.R;
import com.mlt.driver.adapters.RideAdapter;
import com.mlt.driver.helper.SharedPreferencesManager;
import com.mlt.driver.models.Ride;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnCancelled, btnCompleted;
    private SharedPreferencesManager sharedPreferencesManager;
    private RideAdapter rideAdapter;
    private List<Ride> rideList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize SharedPreferencesManager
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());

        // Initialize Views
        recyclerView = view.findViewById(R.id.recyclerView);
        btnCancelled = view.findViewById(R.id.btnCancelled);
        btnCompleted = view.findViewById(R.id.btnCompleted);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideAdapter = new RideAdapter(getContext(), rideList);
        recyclerView.setAdapter(rideAdapter);

        // Load Upcoming Rides
        loadUpcomingRides();

        // Handle Button Clicks
        btnCancelled.setOnClickListener(v -> navigateToFragment1(new CancelledFragment()));
        btnCompleted.setOnClickListener(v -> navigateToFragment2(new CompletedFragment()));

        return view;
    }

    private void loadUpcomingRides() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        try {
            // Prepare the request body
            JSONObject loginRequest = new JSONObject();
            loginRequest.put("api_token", sharedPreferencesManager.getApiToken());
            loginRequest.put("driver_id", sharedPreferencesManager.getUserId());

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), loginRequest.toString());

            // API call
            Call<ResponseBody> call = apiService.getUpcomingRides(requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String jsonResponse = response.body().string();
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            JSONArray upcomingRides = jsonObject.getJSONObject("data").getJSONArray("upcoming_rides");

                            // Populate RecyclerView
                            populateUpcomingRides(upcomingRides);

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
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error preparing request", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateUpcomingRides(JSONArray upcomingRides) {
        rideList.clear();
        try {
            for (int i = 0; i < upcomingRides.length(); i++) {
                JSONObject ride = upcomingRides.getJSONObject(i);

                // Parse Ride details
                Ride newRide = new Ride(
                        ride.getInt("booking_id"),
                        ride.getString("source_address"),
                        ride.getString("dest_address"),
                        ride.getString("journey_date"),
                        ride.getString("journey_time"),
                        ride.getString("ride_status")
                );

                // Add Ride to list
                rideList.add(newRide);
            }

            // Notify adapter of data changes
            rideAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error displaying rides", Toast.LENGTH_SHORT).show();
        }
    }
    private void navigateToFragment1(CancelledFragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void navigateToFragment2(CompletedFragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
