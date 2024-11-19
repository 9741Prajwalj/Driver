package com.mlt.driver.models;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {

    private ApiService apiService;


    public BookingRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public void fetchBookings(String endpoint, String apiToken, int driverId, MutableLiveData<List<Booking>> bookingListLiveData, MutableLiveData<String> errorLiveData) {
        // Create a JSON object for the request body
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("apiToken", "tJ79GzRJcRpPEo59U8k72mmOXW3hjuRqrG577ZYRGu7YVAqdqNLrvLFVaLtu");
            Log.d("chethan", " this is  tokern " +apiToken);
            requestJson.put("driver_id", 168);
        } catch (JSONException e) {
            errorLiveData.setValue("Error creating request body");
            return;
        }

        // Convert JSON object to RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestJson.toString());

        // Make the API call
        Call<ResponseBody> call = apiService.fetchDataFromBackend(requestBody, endpoint);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string(); // Convert response to String
                        Log.d("API_RESPONSE", responseBody);  // Log the raw response for inspection

                        JSONObject jsonResponse = new JSONObject(responseBody); // Proceed if it's valid JSON
                        JSONArray cancelledRides = jsonResponse.getJSONObject("data").getJSONArray("cancelled_rides");

                        List<Booking> bookings = new ArrayList<>();
                        for (int i = 0; i < cancelledRides.length(); i++) {
                            JSONObject ride = cancelledRides.getJSONObject(i);

                            // Fetch optional fields with a default value
                            String totalKms = ride.has("total_kms") ? ride.getString("total_kms") : "0";
                            String amount = ride.has("amount") ? ride.getString("amount") : "0";

                            // Create a Booking object
                            Booking booking = new Booking(
                                    ride.getInt("booking_id"),
                                    ride.getString("book_date"),
                                    ride.getString("source_address"),
                                    ride.getString("dest_address"),
                                    ride.getString("journey_date"),
                                    ride.getString("journey_time"),
                                    ride.getString("ride_status"),
                                    totalKms,
                                    amount
                            );

                            bookings.add(booking);
                        }

                        bookingListLiveData.setValue(bookings);
                    } catch (JSONException | IOException e) {
                        errorLiveData.setValue("Error parsing data");
                        Log.e("API_ERROR", "Error parsing response: " + e.getMessage());
                    }
                } else {
                    errorLiveData.setValue("Failed to load bookings");
                    Log.e("API_ERROR", "API call failed: " + response.code());
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                errorLiveData.setValue("Network error");
            }
        });
    }



    // Helper method to parse the response body into a list of bookings
    private List<Booking> parseBookingsResponse(ResponseBody responseBody) throws JSONException, IOException {
        String responseString = responseBody.string();  // Read the response as a string
        JSONObject jsonResponse = new JSONObject(responseString);  // Create a JSONObject from the string

        JSONArray cancelledRides = jsonResponse.getJSONObject("data").getJSONArray("cancelled_rides");
        List<Booking> bookings = new ArrayList<>();

        for (int i = 0; i < cancelledRides.length(); i++) {
            JSONObject ride = cancelledRides.getJSONObject(i);

            // Fetch optional fields with a default value
            String totalKms = ride.has("total_kms") ? ride.getString("total_kms") : "0";
            String amount = ride.has("amount") ? ride.getString("amount") : "0";

            // Create a Booking object
            Booking booking = new Booking(
                    ride.getInt("booking_id"),
                    ride.getString("book_date"),
                    ride.getString("source_address"),
                    ride.getString("dest_address"),
                    ride.getString("journey_date"),
                    ride.getString("journey_time"),
                    ride.getString("ride_status"),
                    totalKms,
                    amount
            );

            bookings.add(booking);
        }

        return bookings;  // Return the parsed list of bookings
    }

}
