package com.mlt.driver.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    static final String BASE_URL = "https://test.mltcorporate.com"; // Replace with your actual API base URL
    private static final String DIRECTIONS_BASE_URL = "https://test.mltcorporate.com/api/to-pickup";
    private static RetrofitClient instance;
    static Retrofit retrofit;
    private ApiService apiService;
    private static Retrofit googleRetrofit = null;
    static Gson gson = new GsonBuilder().setLenient().create();
    private static final String TAG = "DirectionsApiClient";
    private RetrofitClient() {
        // Initialize Retrofit
        retrofit = buildRetrofit(BASE_URL);  // Use the buildRetrofit method to initialize with logging
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Singleton pattern to get the instance of RetrofitClient
    public static synchronized RetrofitClient getInstance(String baseUrl) {
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
