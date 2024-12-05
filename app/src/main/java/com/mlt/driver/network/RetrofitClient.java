package com.mlt.driver.network;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    static final String BASE_URL = "https://ets.mltcorporate.com"; // Replace with your actual API base URL
    private static final String DIRECTIONS_BASE_URL = "https://ets.mltcorporate.com/api/to-pickup";
    private static RetrofitClient instance;
    static Retrofit retrofit;
    private ApiService apiService;
    private static Retrofit googleRetrofit = null;
    static Gson gson = new GsonBuilder().setLenient().create();
    private static final String TAG = "DirectionsApiClient";
    private static final String DIRECTIONS_API_BASE_URL = "https://ets.mltcorporate.com/api/to-pickup";

    private RetrofitClient() {
        // Initialize Retrofit
        retrofit = buildRetrofit(BASE_URL);  // Use the buildRetrofit method to initialize with logging
    }

    // Singleton pattern to get the instance of RetrofitClient
    public static synchronized RetrofitClient getInstance(String baseUrl) {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

//    public static String getRoutePolyline(LatLng origin, LatLng destination, String apiKey) {
//        try {
//            String urlString = DIRECTIONS_API_BASE_URL + "?origin=" + origin.latitude + "," + origin.longitude
//                    + "&destination=" + destination.latitude + "," + destination.longitude
//                    + "&mode=driving&key=" + apiKey;
//
//            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
//            connection.setRequestMethod("GET");
//            connection.connect();
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                Scanner scanner = new Scanner(connection.getInputStream());
//                StringBuilder response = new StringBuilder();
//                while (scanner.hasNextLine()) {
//                    response.append(scanner.nextLine());
//                }
//                scanner.close();
//                connection.disconnect();
//
//                // Extract the polyline from the response
//                String polyline = extractPolylineFromResponse(response.toString());
//                if (polyline != null) {
//                    return polyline;
//                } else {
//                    Log.e(TAG, "Polyline not found in the response.");
//                    return null;
//                }
//            } else {
//                Log.e(TAG, "HTTP error code: " + responseCode);
//                return null;
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "Error fetching route data", e);
//            return null;
//        }
//    }
//
//    private static String extractPolylineFromResponse(String jsonResponse) {
//        // Simple JSON parsing logic to extract the polyline encoded string
//        try {
//            JSONObject jsonObject = new JSONObject(jsonResponse);
//            JSONArray routes = jsonObject.getJSONArray("routes");
//            if (routes.length() > 0) {
//                JSONObject route = routes.getJSONObject(0);
//                JSONArray legs = route.getJSONArray("legs");
//                if (legs.length() > 0) {
//                    JSONObject leg = legs.getJSONObject(0);
//                    JSONObject steps = leg.getJSONArray("steps").getJSONObject(0);
//                    return steps.getJSONObject("polyline").getString("points");
//                }
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing JSON response", e);
//        }
//        return null;
//    }

    // Method to build Retrofit with logging
    private static Retrofit buildRetrofit(String baseUrl) {
        // Create logging interceptor to log the request and response details
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);  // Log both request and response bodies
        // Create OkHttpClient with the logging interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)  // Add the logging interceptor to OkHttp client
                .build();
        // Return Retrofit instance with logging enabled and a custom Gson converter
        return new Retrofit.Builder()
                .baseUrl(baseUrl)  // Set the base URL
                .client(client)  // Set the OkHttpClient with the logging interceptor
                .addConverterFactory(GsonConverterFactory.create(gson))  // Use the custom Gson converter
                .build();
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = buildRetrofit(BASE_URL);
        }
        return retrofit;
    }
    // Get Retrofit instance for the main API
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = buildRetrofit(BASE_URL);  // Initialize with the main base URL
        }
        return retrofit;
    }
    public ApiService getApiService() {
        return apiService;
    }
    // Creates a specified service class (generic method)
    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);  // Create the service class
    }
}
