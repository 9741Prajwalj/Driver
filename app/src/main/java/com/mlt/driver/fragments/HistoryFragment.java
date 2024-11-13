package com.mlt.driver.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mlt.driver.R;
import com.mlt.driver.activities.CancelDialogActivity;
import com.mlt.driver.models.TripDetails;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;
import com.mlt.driver.utills.MyButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HistoryFragment extends Fragment {

    private ImageView backArrow;
    private TextView tripProviderName, tripDate, tripSource, tripDestination, tripId, tripComments;
    private RatingBar tripProviderRating;
    private MyButton btnStartRide, btnCancelRide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize views
        tripProviderName = view.findViewById(R.id.tripProviderName);
        tripDate = view.findViewById(R.id.tripDate);
        tripSource = view.findViewById(R.id.tripSource);
        tripDestination = view.findViewById(R.id.tripDestination);
        tripId = view.findViewById(R.id.trip_id);
        tripComments = view.findViewById(R.id.tripComments);
        tripProviderRating = view.findViewById(R.id.tripProviderRating);
        btnStartRide = view.findViewById(R.id.btnStartRide);
        btnCancelRide = view.findViewById(R.id.btnCancelRide);

        // Set static details
        setStaticTripDetails();

        // Start Ride button click listener
        btnStartRide.setOnClickListener(v -> {
            showMap();
            shareDriverDataToBackend();
        });

        // Cancel Ride button click listener
        btnCancelRide.setOnClickListener(v -> navigateToCancelDialog());

        // Fetch dynamic data from backend
        fetchTripDetailsFromBackend();

        return view;
    }

    private void setStaticTripDetails() {
        tripProviderName.setText("Provider XYZ");
        tripProviderRating.setRating(4.5f);
        tripDate.setText("2024-11-08");
        tripSource.setText("Source Location");
        tripDestination.setText("Destination Location");
        tripId.setText("12345");
        tripComments.setText("Trip was smooth and timely.");
    }

    private void navigateToMapFragment() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new MapFragment()); // Adjust as per your container ID
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showMap() {
        // Code to display map
        // You can also implement map functionality here
    }

    private void navigateToCancelDialog() {
        Intent intent = new Intent(getActivity(), CancelDialogActivity.class);
        startActivity(intent);
    }

    private void shareDriverDataToBackend() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("DriverData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Sample data
        editor.putString("username", "DriverJohn");
        editor.putString("userId", "D123");
        editor.putString("userPhoneNumber", "123-456-7890");
        editor.apply();
    }

    private void fetchTripDetailsFromBackend() {
        Retrofit retrofit = RetrofitClient.getInstances(); // Assuming you have a RetrofitClient class
        ApiService apiService = retrofit.create(ApiService.class);
//        ApiService apiService = RetrofitClient.getClient().create(ApiService.class); // Ensure ApiServer is your correct interface name
        Call<TripDetails> call = apiService.getTripDetails(); // Adjust endpoint if needed

        call.enqueue(new Callback<TripDetails>() {
            @Override
            public void onResponse(@NonNull Call<TripDetails> call, @NonNull Response<TripDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TripDetails tripDetails = response.body();

                    // Ensure UI components are set correctly with backend data
                    tripProviderName.setText(tripDetails.getProviderName());
                    tripProviderRating.setRating(tripDetails.getProviderRating());
                    tripDate.setText(tripDetails.getDate());
                    tripSource.setText(tripDetails.getSource());
                    tripDestination.setText(tripDetails.getDestination());
                    tripId.setText(tripDetails.getTripId());
                    tripComments.setText(tripDetails.getComments());
                    // Log success message
                    Log.d("TripDetails", "Trip details successfully fetched from backend.");

                } else {
                    // Handle unsuccessful response
                    Log.e("TripDetails", "Failed to fetch trip details: " + response.message());
//                    Toast.makeText(getApplicationContext(), "Failed to load trip details. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TripDetails> call, @NonNull Throwable t) {
                // Handle failure
                Log.e("TripDetails", "Error fetching trip details: " + t.getMessage(), t);
//                Toast.makeText(getApplicationContext(), "Network error. Please check your connection and try again.", Toast.LENGTH_LONG).show();
            }
        });
    }
}

