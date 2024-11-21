package com.mlt.driver.fragments;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.driver.R;
import com.mlt.driver.adapters.CancelledRideAdapter;
import com.mlt.driver.helper.SharedPreferencesManager;
import com.mlt.driver.models.CancelledRide;
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

public class CancelledFragment extends Fragment {

    private RecyclerView recyclerView;
    private SharedPreferencesManager sharedPreferencesManager;

    private CancelledRideAdapter adapter;
    private List<CancelledRide> rideList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canceled, container, false);
        // Initialize SharedPreferencesManager
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());

        recyclerView = view.findViewById(R.id.cancelledContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CancelledRideAdapter(rideList);
        recyclerView.setAdapter(adapter);

        fetchCancelledRides();

        return view;
    }

    private void fetchCancelledRides() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        JSONObject requestBodyJson = new JSONObject();

        try {
            // Prepare the request body
            JSONObject loginRequest = new JSONObject();
            loginRequest.put("api_token", sharedPreferencesManager.getApiToken());
            loginRequest.put("driver_id", sharedPreferencesManager.getUserId());

            // Construct JSON request body if necessary
            requestBodyJson.put("key", "value"); // Replace with actual key-value pairs if needed

            RequestBody requestBody = RequestBody.create(
                    requestBodyJson.toString(),
                    MediaType.parse("application/json")
            );

            Call<ResponseBody> call = apiService.getCanceledRides(requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            JSONArray cancelledRides = jsonResponse.getJSONObject("data").getJSONArray("cancelled_rides");

                            for (int i = 0; i < cancelledRides.length(); i++) {
                                JSONObject rideJson = cancelledRides.getJSONObject(i);

                                CancelledRide ride = new CancelledRide();
                                ride.setBookingId(rideJson.getInt("booking_id"));
                                ride.setBookDate(rideJson.getString("book_date"));
                                ride.setBookTime(rideJson.getString("book_time"));
                                ride.setSourceAddress(rideJson.getString("source_address"));
                                ride.setDestAddress(rideJson.getString("dest_address"));
                                ride.setJourneyDate(rideJson.getString("journey_date"));
                                ride.setJourneyTime(rideJson.getString("journey_time"));
                                ride.setRideStatus(rideJson.getString("ride_status"));

                                rideList.add(ride);
                            }

                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed to parse response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch rides", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

