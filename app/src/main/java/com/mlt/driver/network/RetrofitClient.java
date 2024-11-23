package com.mlt.driver.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    static final String BASE_URL = "https://ets.mltcorporate.com"; // Replace with your actual API base URL
    private static final String DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static RetrofitClient instance;
    static Retrofit retrofit;
    private static Retrofit googleRetrofit = null;
    private RetrofitClient() {
        // Initialize Retrofit
        retrofit = buildRetrofit(BASE_URL);  // Use the buildRetrofit method to initialize with logging
    }
    // Singleton pattern to get the instance of RetrofitClient
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    // Method to build Retrofit with logging
    private static Retrofit buildRetrofit(String baseUrl) {
        // Create logging interceptor to log the request and response details
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);  // Log both request and response bodies
        // Create OkHttpClient with the logging interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)  // Add the logging interceptor to OkHttp client
                .build();
        // Return Retrofit instance with logging enabled
        return new Retrofit.Builder()
                .baseUrl(baseUrl)  // Set the base URL
                .client(client)  // Set the OkHttpClient with the logging interceptor
                .addConverterFactory(GsonConverterFactory.create())  // Use Gson converter for response
                .build();
    }
    // Get Retrofit instance for the main API
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = buildRetrofit(BASE_URL);  // Initialize with the main base URL
        }
        return retrofit;
    }
    // Get Retrofit instance for the Google API (for directions or other services)
    public static Retrofit getGoogleClient() {
        if (googleRetrofit == null) {
            googleRetrofit = buildRetrofit(DIRECTIONS_BASE_URL);  // Initialize with the Google Directions API URL
        }
        return googleRetrofit;
    }
    // Creates a specified service class (generic method)
    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);  // Create the service class
    }
    // Get the ApiService instance for the main API
    public ApiService getApi() {
        return retrofit.create(ApiService.class);  // Create the ApiService for the main API
    }
}
