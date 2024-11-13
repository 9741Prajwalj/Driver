package com.mlt.driver.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    static final String BASE_URL = "https://ets.mltcorporate.com"; // Replace with your actual API base URL
    private static final String DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/";

    private static RetrofitClient instance;
    static Retrofit retrofit;
//    private static Retrofit retrofit = null;
    private static Retrofit googleRetrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    private RetrofitClient() {
        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // For JSON conversion
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getInstances() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Creates a specified service class (generic method)
    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public ApiService getApi() {
        return retrofit.create(ApiService.class);
    }
}
